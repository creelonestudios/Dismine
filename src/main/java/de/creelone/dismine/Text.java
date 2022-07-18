package de.creelone.dismine;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Text {

	public String s;
	public boolean italics;
	public boolean bold;
	public boolean underline;
	public boolean strikethrough;
	public boolean spoiler;
	public boolean code;

	public Text(String s, boolean italics, boolean bold, boolean underline, boolean strikethrough, boolean spoiler, boolean code) {
		this.s = s;
		this.italics = italics;
		this.bold = bold;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.spoiler = spoiler;
		this.code = code;
	}

	public Text() {
		this("", false, false, false, false, false, false);
	}

	public TextComponent asComponent() {
		TextComponent comp = Component.text(s).color(TextColor.color(code ? 0x404040 : 0xb0b0b0));
		if (italics) comp = comp.decorate(TextDecoration.ITALIC);
		if (bold) comp = comp.decorate(TextDecoration.BOLD);
		if (underline) comp = comp.decorate(TextDecoration.UNDERLINED);
		if (strikethrough) comp = comp.decorate(TextDecoration.STRIKETHROUGH);
		if (spoiler) comp = comp.decorate(TextDecoration.OBFUSCATED);
		return comp;
	}

	public String toString() {
		String style = "";
		if (italics) style += "italics,";
		if (bold) style += "bold,";
		if (underline) style += "underline,";
		if (strikethrough) style += "strikethrough,";
		if (spoiler) style += "spoiler,";
		if (code) style += "code,";
		if (style.endsWith(",")) style = style.substring(0, style.length() -1);
		return String.format("Text{s:%s,style:%s}", s, style);
	}

}
