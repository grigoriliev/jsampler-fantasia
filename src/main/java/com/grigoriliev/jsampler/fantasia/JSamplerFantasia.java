package com.grigoriliev.jsampler.fantasia;

import com.grigoriliev.jsampler.JSampler;
import com.grigoriliev.jsampler.fantasia.view.MainFrame;

public class JSamplerFantasia {
	public static void main(String[] args) {
		MainFrame.installDesktopHandlers();
		JSampler.main(args);
	}
}
