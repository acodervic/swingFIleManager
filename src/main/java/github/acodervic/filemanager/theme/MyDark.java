package github.acodervic.filemanager.theme;

import com.formdev.flatlaf.FlatDarkLaf;

public class MyDark
	extends FlatDarkLaf
{
	public static final String NAME = "MyDark";

	public static boolean setup() {
		return setup( new MyDark() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, MyDark.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
