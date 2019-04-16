package de.pt400c.defaultsettings.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.relauncher.IClassTransformer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.server.MinecraftServer;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class DefaultSettingsClassTransformer implements IClassTransformer {

	private static final RelationObject[] classesToTransform = { new RelationObject("net.minecraft.client.Minecraft", "net.minecraft.client.Minecraft") };
	public static final boolean devEnv = (Boolean) Arrays.asList(MinecraftServer.class.getDeclaredFields()).get(8).getName().equals("hostname");
	public static boolean gameObf;
	
	@Override
	public byte[] transform(String name, byte[] classToTransform) {

		int c_index = -1;
		
		for(int i = 0; i < classesToTransform.length; i++) {
			if(classesToTransform[i].dev.equals(name) || classesToTransform[i].obf.equals(name)) {
				c_index = i;
				break;
			}
		}
		
		if (!gameObf && !devEnv)
			gameObf = true;

		return c_index != -1 ? transform(c_index, classToTransform, gameObf, classesToTransform[c_index].dev, classesToTransform[c_index].obf) : classToTransform;
	}

	private byte[] transform(int c_index, byte[] classToTransform, boolean isObfuscated, String name, String transformedName) {

		System.out.println("Transforming now: " + classesToTransform[c_index].dev);

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
		final String CLASS_NAME = isObfuscated ? "J" : "runGameLoop";
		final String CLASS_DESC_OBF = "()V";
		List<MethodNode> s = mainClass.methods;
		for (MethodNode method : s) {

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
					method.instructions.remove(targetNode);
				}			
				
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == ALOAD && instruction.getNext().getOpcode() == INVOKESPECIAL && instruction.getNext().getNext().getOpcode() == INVOKESTATIC && instruction.getNext().getNext().getNext().getOpcode() == INVOKESTATIC) {
						targetNode = instruction;
						break;
					}

				}

				if (targetNode != null) {
					
					method.instructions.set(targetNode.getNext(), new MethodInsnNode(INVOKESTATIC, "de/pt400c/defaultsettings/TickHandlerClient", "getLimitFramerate", "()I"));
					
					method.instructions.remove(targetNode.getNext().getNext());
					
					method.instructions.remove(targetNode);
					System.out.println("Transformed framerate method!");
					
				}
				
				break;
			}
		}
	}
}

class RelationObject {
	public final String obf;
	public final String dev;
	
	public RelationObject(String obf, String dev) {
		this.obf = obf;
		this.dev = dev;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RelationObject && this.obf == ((RelationObject) obj).obf && this.dev == ((RelationObject )obj).dev;
		
	}
}