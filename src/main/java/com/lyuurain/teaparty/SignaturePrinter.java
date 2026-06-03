package com.lyuurain.teaparty;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SignaturePrinter {
    public static void printSignatures() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("C:\\game\\ai\\gemlike_teaparty\\signatures.txt"))) {
            writer.println("=== BaseEntityBlock Methods ===");
            printClassMethods(writer, "net.minecraft.world.level.block.BaseEntityBlock");

            writer.println("\n=== BlockBehaviour Methods ===");
            printClassMethods(writer, "net.minecraft.world.level.block.state.BlockBehaviour");

            writer.println("\n=== Block Methods ===");
            printClassMethods(writer, "net.minecraft.world.level.block.Block");

            writer.println("\n=== BlockEntity Methods ===");
            printClassMethods(writer, "net.minecraft.world.level.block.entity.BlockEntity");

            writer.println("\n=== BlockStateBase Methods ===");
            printClassMethods(writer, "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase");

            writer.println("\n=== ItemInteractionResult Methods ===");
            printClassMethods(writer, "net.minecraft.world.ItemInteractionResult");

            writer.println("\n=== VertexConsumer Methods ===");
            printClassMethods(writer, "com.mojang.blaze3d.vertex.VertexConsumer");

            writer.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printClassMethods(PrintWriter writer, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Modifier.toString(method.getModifiers())).append(" ");
                    sb.append(method.getReturnType().getName()).append(" ");
                    sb.append(method.getName()).append("(");
                    Class<?>[] params = method.getParameterTypes();
                    for (int i = 0; i < params.length; i++) {
                        sb.append(params[i].getName());
                        if (i < params.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(")");
                    writer.println(sb.toString());
                }
            }
        } catch (ClassNotFoundException e) {
            writer.println("Class not found: " + className);
        }
    }
}
