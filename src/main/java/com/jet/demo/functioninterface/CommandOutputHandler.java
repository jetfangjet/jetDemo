package com.jet.demo.functioninterface;

@FunctionalInterface
public interface CommandOutputHandler {
    public void dealResult(String output);
}
