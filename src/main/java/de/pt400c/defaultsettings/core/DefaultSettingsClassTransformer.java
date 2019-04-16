package de.pt400c.defaultsettings.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;
import java.util.Arrays;

public class DefaultSettingsClassTransformer implements IClassTransformer {

	private static final String[] classesToTransform = { "net.minecraft.client.Minecraft"};

	public static boolean gameObf;

	@Override
	public byte[] transform(String name, String transformedName, byte[] classToTransform) {
		boolean isObfuscated = !name.equals(transformedName);

		int c_index = Arrays.asList(classesToTransform).indexOf(transformedName);
		if (!gameObf && isObfuscated)
			gameObf = true;

		return c_index != -1 ? transform(c_index, classToTransform, isObfuscated, name, transformedName) : classToTransform;
	}

	private byte[] transform(int c_index, byte[] classToTransform, boolean isObfuscated, String name, String transformedName) {

		System.out.println("Transforming now: " + classesToTransform[c_index]);

		try {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classToTransform);
			classReader.accept(classNode, 0);

			switch (c_index) {
			case 0:
				transformDefaultFramerate(classNode, isObfuscated);
				break;
			}

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return classToTransform;
	}

	private static void transformDefaultFramerate(ClassNode mainClass, boolean isObfuscated) {
		final String CLASS_NAME = isObfuscated ? "func_71411_J" : "runGameLoop";
		final String CLASS_DESC_OBF = "()V";

		for (MethodNode method : mainClass.methods) {

			if (method.name.equals(CLASS_NAME) && method.desc.equals(CLASS_DESC_OBF)) {
				
				AbstractInsnNode targetNode = null;

				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ALOAD && instruction.getNext().getOpcode() == INVOKESPECIAL && instruction.getNext().getNext().getOpcode() == IFLE) {
						targetNode = instruction;
						break;
					}

				}

				if (targetNode != null) {
					
					method.instructions.set(targetNode.getNext(), new MethodInsnNode(INVOKESTATIC, "de/pt400c/defaultsettings/TickHandlerClient", "getLimitFramerate", "()I"));
					
				}
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ALOAD && instruction.getNext().getOpcode() == INVOKESPECIAL && instruction.getNext().getNext().getOpcode() == INVOKESTATIC && instruction.getNext().getNext().getNext().getOpcode() == INVOKESTATIC) {
						targetNode = instruction;
						break;
					}

				}

				if (targetNode != null) {
					
					method.instructions.remove(targetNode.getNext().getNext());
					
					method.instructions.remove(targetNode);
					
				}
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKESPECIAL && instruction.getNext().getOpcode() == INVOKESTATIC && ((MethodInsnNode) instruction.getNext()).name.equals("sync")) {
						targetNode = instruction;
						break;
					}

				}

				if (targetNode != null) {	
					
					method.instructions.set(targetNode, new MethodInsnNode(INVOKESTATIC, "de/pt400c/defaultsettings/TickHandlerClient", "getLimitFramerate", "()I"));
					System.out.println("Transformed framerate method!");
				}
				
				break;
			}
		}
	}

}