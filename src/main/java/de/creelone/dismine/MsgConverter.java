package de.creelone.dismine;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;

public class MsgConverter {

	static String convertToDC(String s) {
		s = s.replaceAll("§.", "");
		return s;
	}

	static TextComponent convertToMC(String s) {
		var texts = parseText(s, 0, null);
		var comp = Component.empty();
		for (var text : texts) {
			comp = comp.append(text.asComponent());
		}
		//Dismine.instance.getLogger().log(Level.INFO, "texts:" + texts);
		return comp;
	}

	static ArrayList<Text> parseText(String x, int i, String end) {
		ArrayList<Text> arr = new ArrayList<>();
		Text text = new Text();

		boolean italics = false;
		boolean bold = false;
		boolean underline = false;
		boolean strikethrough = false;
		boolean spoiler = false;

		while (i < x.length() || (end != null && substr(x, i, end.length()).equals(end))) {
			//Dismine.instance.getLogger().log(Level.INFO, String.format("parse: %s, %s, %s: %s %s%s, %s", x, i, end, c(x,i), c(x,i+1), c(x,i+2), text));
			if (c(x,i).equals("\\")) {
				text.s += c(x,i+1);
				i += 2;
				continue;
			}

			if (c(x,i).equals("*")) {
				var amount = 1;
				i++;
				while (amount < 3 && c(x,i).equals("*")) {
					amount++;
					i++;
				}
				if (text.s.length() > 0) arr.add(text);
				if (amount % 2 == 1) italics = !italics; // 1 or 3
				if (amount     >= 2) bold    = !bold;    // 2 or 3
				text = new Text("", italics, bold, underline, strikethrough, spoiler, false);
				continue;
			}

			if (c(x,i).equals("_")) {
				var amount = 1;
				i++;
				while (amount < 3 && c(x,i).equals("_")) {
					amount++;
					i++;
				}
				if (text.s.length() > 0) arr.add(text);
				if (amount % 2 == 1) italics   = !italics;   // 1 or 3
				if (amount     >= 2) underline = !underline; // 2 or 3
				text = new Text("", italics, bold, underline, strikethrough, spoiler, false);
				continue;
			}

			// TODO: fix (end not detected for some reason)
			//Dismine.instance.getLogger().log(Level.INFO, String.format("parse: strike: %s, %s", substr(x,i,2).equals("~~"), strikethrough));
			if (c(x,i).equals("~")) {
				var amount = 1;
				i++;
				while (amount < 2 && c(x,i).equals("")) {
					amount++;
					i++;
				}
				if (amount == 1) {
					text.s += "~";
					i++;
					continue;
				}
				if (text.s.length() > 0) arr.add(text);
				strikethrough = !strikethrough;
				text = new Text("", italics, bold, underline, strikethrough, spoiler, false);
				continue;
			}

			if (substr(x,i,2).equals("||")) {
				i += 2;
				if (text.s.length() > 0) arr.add(text);
				spoiler = !spoiler;
				text = new Text("", italics, bold, underline, strikethrough, spoiler, false);
				continue;
			}

			// TODO: code and spoiler

			text.s += c(x,i);
			i++;
		}

		if (text.s.length() > 0) arr.add(text);

		return arr;
	}

	// TODO: fix this
	/*private static TextComponent parseText(String msg, boolean italics, boolean bold, boolean underline, boolean strikethrough, boolean spoiler) {
		TextComponent comp = Component.text("");
		String buffer = "";

		int i = 0;
		while (i < msg.length()) {
			var c = charAt(msg,i);
			Dismine.instance.getLogger().log(Level.INFO, String.format("%s:%s - %s (%s)", msg, i,c, buffer));
			if (c.equals("\\")) {
				c = charAt(msg,i+1);
				buffer += c;
				i += 2;
				continue;
			}

			if (c.equals("`")) {
				if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, false));
				buffer = "";

				var amount = 1;
				if (charAt(msg,i+1).equals("`")) amount = 2;
				if (charAt(msg,i+1).equals("`") && charAt(msg,i+1).equals("`")) amount = 3;
				var s = "";
				i += amount;
				while (i < msg.length()) {
					var c0 = charAt(msg,i);
					var c1 = charAt(msg,i+1);
					var c2 = charAt(msg,i+2);
					if (amount == 1 && c0.equals("`")) break;
					if (amount == 2 && c0.equals("`") && c1.equals("`")) break;
					if (amount == 3 && c0.equals("`") && c1.equals("`") && c2.equals("`")) break;
					s += c0;
					i++;
				}
				if (i >= msg.length()) {
					comp = comp.append(createText("`".repeat(amount) + s, italics, bold, underline, strikethrough, spoiler, false));
					break;
				}
				comp = comp.append(createText(s, italics, bold, underline, strikethrough, spoiler, true));
				i += amount;
				continue;
			}

			if (c.equals("~") && charAt(msg,i+1).equals("~")) {
				if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, false));
				buffer = "";

				var s = "";
				i += 2;
				while (i < msg.length()) {
					var c0 = charAt(msg,i);
					var c1 = charAt(msg,i+1);
					if (c0.equals("~") && c1.equals("~")) break;
					s += c0;
					i++;
				}
				i += 2;
				if (i >= msg.length()) {
					comp = comp.append(createText("~~", italics, bold, underline, strikethrough, spoiler, false));
					comp = comp.append(parseText(s, italics, bold, underline, strikethrough, spoiler));
					break;
				}
				comp = comp.append(parseText(s, italics, bold, underline, true, spoiler));
				continue;
			}

			if (c == "_") {
				var amount = 1;
				if (charAt(msg,i+1).equals("_")) amount = 2;
				if (charAt(msg,i+1).equals("_") && charAt(msg,i+2).equals("_")) amount = 3;
				var s = "";
				while (i < msg.length()) {
					var c0 = charAt(msg,i);
					var c1 = charAt(msg,i+1);
					var c2 = charAt(msg,i+2);
					if (amount == 1 && c0.equals("_")) break;
					if (amount == 2 && c0.equals("_") && c1.equals("_")) break;
					if (amount == 3 && c0.equals("_") && c1.equals("_") && c2.equals("_")) break;
					s += c0;
				}
				if (i >= msg.length()) {
					comp = comp.append(createText("_".repeat(amount), italics, bold, underline, strikethrough, spoiler, false));
					comp = comp.append(parseText(s, italics, bold, underline, strikethrough, spoiler));
					break;
				}
				if (amount == 1) comp = comp.append(parseText(s, true, bold, underline, strikethrough, spoiler));
				if (amount == 2) comp = comp.append(parseText(s, italics, bold, true, strikethrough, spoiler));
				if (amount == 3) comp = comp.append(parseText(s, true, bold, true, strikethrough, spoiler));
				i += amount;
				continue;
			}

			if (c.equals("*")) {
				var amount = 1;
				if (charAt(msg,i+1).equals("*")) amount = 2;
				if (charAt(msg,i+1).equals("*") && charAt(msg,i+2).equals("*")) amount = 3;
				var s = "";
				while (i < msg.length()) {
					var c0 = charAt(msg,i);
					var c1 = charAt(msg,i+1);
					var c2 = charAt(msg,i+2);
					if (amount == 1 && c0.equals("*")) break;
					if (amount == 2 && c0.equals("*") && c1 == "*") break;
					if (amount == 3 && c0.equals("*") && c1.equals("*") && c2.equals("*")) break;
					s += c0;
				}
				if (i >= msg.length()) {
					comp = comp.append(createText("*".repeat(amount), italics, bold, underline, strikethrough, spoiler, false));
					comp = comp.append(parseText(s, italics, bold, underline, strikethrough, spoiler));
					break;
				}
				if (amount == 1) comp = comp.append(parseText(s, true, bold, underline, strikethrough, spoiler));
				if (amount == 2) comp = comp.append(parseText(s, italics, true, underline, strikethrough, spoiler));
				if (amount == 3) comp = comp.append(parseText(s, true, true, underline, strikethrough, spoiler));
				i += amount;
				continue;
			}

			if (c.equals("|") && charAt(msg,i+1).equals("|")) {
				if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, false));
				buffer = "";

				var s = "";
				i += 2;
				while (i < msg.length()) {
					var c0 = charAt(msg,i);
					var c1 = charAt(msg,i+1);
					if (c0.equals("|") && c1.equals("|")) break;
					s += c0;
					i++;
				}
				i += 2;
				if (i >= msg.length()) {
					comp = comp.append(createText("||", italics, bold, underline, strikethrough, spoiler, false));
					comp = comp.append(parseText(s, italics, bold, underline, strikethrough, spoiler));
					break;
				}
				comp = comp.append(parseText(s, italics, bold, underline, strikethrough, true));
				continue;
			}

			buffer += c;
			i++;
		}

		if (buffer.length() > 0) comp = comp.append(createText(buffer, italics, bold, underline, strikethrough, spoiler, false));

		return comp;
	}*/

	private static String c(String s, int index) {
		if (index < 0 || index >= s.length()) return "";
		return "" + s.charAt(index);
	}

	private static String substr(String s, int index, int length) {
		var x = new String[length];
		for (var i = index; i < length && i < s.length(); i++) {
			x[i-index] = c(s, i);
		}
		return String.join("", x);
	}

}