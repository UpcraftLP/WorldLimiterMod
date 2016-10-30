package mod.upcraftlp.worldlimiter.util;

import mod.upcraftlp.worldlimiter.Reference;
import net.minecraftforge.fml.common.FMLLog;

public class SysUtils {

	public static void println(String output)
	{
		System.out.println("[" + Reference.MOD_ID + "] " + output);
	}
	
	public static void printErr(String output) {
		System.err.println(ANSI_RED + "[" + Reference.MOD_ID + "] " + output);
	}
	
	public static void printlineFML(String output) {
		FMLLog.info("[" + Reference.MOD_ID + "] " + output);
	}
	
	public static void printlineFMLERROR(String output) {
		FMLLog.severe("[" + Reference.MOD_ID + "] " + output);
	}
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
}
