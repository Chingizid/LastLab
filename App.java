package com.collecton2.reflection2;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException 
    {
    	InjectorOther injector = new InjectorOther("config.properties");
        SomeBean sb = injector.inject(new SomeBean());
        sb.foo(); 
        System.out.println();
        injector = new InjectorOther("config2.properties");
        sb = injector.inject(new SomeBean());
        sb.foo();
    }
}
