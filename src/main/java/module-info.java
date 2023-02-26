module com.grigoriliev.jsampler.fantasia {
	requires com.grigoriliev.jsampler;
	requires com.grigoriliev.jsampler.jlscp;
	requires com.grigoriliev.jsampler.juife;
	requires com.grigoriliev.jsampler.juife.swing;
	requires com.grigoriliev.jsampler.swing;

	requires java.desktop;
	requires java.logging;

	requires substance;
	requires swingx;

	requires static orange.extensions;

	exports com.grigoriliev.jsampler.fantasia.view;
}