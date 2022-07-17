package de.creelone.dismine;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MsgConverter {

	static String convertToDC(String s) {
		s = s.replace("\\", "\\\\");
		s = s.replaceAll("§.", "");
		return s;
	}

	static TextComponent convertToMC(String s) {
		TextComponent comp = Component.text("");
		boolean italics = false;
		boolean bold = false;
		boolean underline = false;
		boolean strikethrough = false;
		boolean spoiler = false;
		boolean code = false;
		String buffer = "";

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			//if (e)
			if (c == '*') {
				if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, code));
				italics = !italics;
				buffer = "";
			} else buffer += c;
		}

		if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, code));

		return comp;
	}

	private static TextComponent createText(String s, boolean italics, boolean bold, boolean underline, boolean strikethrough, boolean spoiler, boolean code) {
		TextComponent comp = Component.text(s)
				.color(TextColor.color(code ? 0x404040 : 0xb0b0b0));
		if(italics)       comp = comp.decorate(TextDecoration.ITALIC);
		if(bold)          comp = comp.decorate(TextDecoration.BOLD);
		if(underline)     comp = comp.decorate(TextDecoration.UNDERLINED);
		if(strikethrough) comp = comp.decorate(TextDecoration.STRIKETHROUGH);
		if(spoiler)       comp = comp.decorate(TextDecoration.OBFUSCATED);
		return comp;
	}

}
