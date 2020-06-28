package com.jet.demo.Utils;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jet.demo.functioninterface.CommandOutputHandler;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;

/**
 * 本工具类用于执行命令行
 * 基于java8，因为有函数式编程
 */
public class CommandExecuteUtil {
//    private ExecutorService executorService = ExecutorService
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        Properties props = System.getProperties();
        System.out.println("操作系统的名称：" + props.getProperty("os.name"));
        System.out.println("操作系统的版本号：" + props.getProperty("os.version"));

        // 下面是阻塞式执行命令行，你可以试试执行netstat -na，肯定会卡死
        String output = executeCommand("ping www.baidu.com", new File("D:/"));
        System.out.println(output);
//        System.out.println(executeCommand("netstat -na"));
        // 下面是非阻塞式执行命令行
        String command = "netstat -na";
        executeCommandUnblock(command);
    }
    public static String executeCommand(String command) {
        return executeCommand(command, null);
    }
    /*
     * 执行dos命令的方法
     * @param command 需要执行的dos命令
     * @param file 指定开始执行的文件目录
     * 阻塞执行
     * @return true 转换成功，false 转换失败
     */
    private static void executeRunnable(Runnable runnable){
        // 用线程池的话，单次运行main方法的话进程不会结束，测试还是用new Thread
//        executorService.execute(runnable);
        new Thread(runnable).start();
    }

    public static String executeCommand(String command, File file) {
        StringBuffer output = new StringBuffer();
        Process p;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            p = Runtime.getRuntime().exec(command, null, file);
            p.waitFor();
            inputStreamReader = new InputStreamReader(p.getInputStream(), "GBK");
            reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(inputStreamReader);
        }
        System.out.println(output.toString());
        return output.toString();
    }

    /**
     * 非阻塞式执行命令行
     * @param command
     */
    public static void executeCommandUnblock(String command){
        executeCommandUnblock(command, null);
    }

    /**
     * 非阻塞式执行命令行
     * @param command
     */
    public static void executeCommandUnblock(String command, File file) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, file);
            // 处理命令行输出结果
            executeRunnable(new OutputHandlerRunnable(p.getInputStream(), (String result)->{
                if(StringUtils.isEmpty(result)){
                    System.out.println("no output");
                }else {
                    System.out.println("output=" + result);
                }
            }));
            // 处理命令行错误结果
            executeRunnable(new OutputHandlerRunnable(p.getErrorStream(), (String result)->{
                if(StringUtils.isEmpty(result)){
                    System.out.println("no error");
                }else{
                    System.out.println("error="+result);
                }

            }));
            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private static class OutputHandlerRunnable implements Runnable {
        private InputStream in;
        private CommandOutputHandler commandOutputHandler;
        public OutputHandlerRunnable(InputStream in, CommandOutputHandler commandOutputHandler) {
            this.in = in;
            this.commandOutputHandler = commandOutputHandler;
        }

        @Override
        public void run() {
            StringBuffer output = new StringBuffer();
            String osName = System.getProperties().getProperty("os.name");
            String charset = "UTF-8";
            // windows的命令行是gbk编码
            if(osName.startsWith("Windows")){
                charset = "GBK";
            }
            try (BufferedReader bufr = new BufferedReader(new InputStreamReader(this.in, charset))) {
                String line = null;
                // 将流写到字符串，清空其缓冲区
                while ((line = bufr.readLine()) != null) {
                    output.append(line).append("\n");
//                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            commandOutputHandler.dealResult(output.toString());
        }
    }
}
