package nl.pascalroeleven.minecraft.mineshotrevived.client.config;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

public class MineshotConfigScreen extends GameOptionsScreen {
	private PropertiesHandler properties = Mineshot.getPropertiesHandler();
	private OptionsListWidget optionsListWidget;
	private TextFieldWidget captureWidth;
	private TextFieldWidget captureHeight;
	private CheckboxWidget notifyDev;
	private CheckboxWidget notifyIncompatible;

	protected MineshotConfigScreen(Screen parent, GameOptions options) {
		super(parent, options, new TranslatableText("mineshotrevived.config.title"));
	}

	@Override
	protected void init() {
		this.optionsListWidget = new OptionsListWidget();
		this.addSelectableChild(this.optionsListWidget);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20,
				ScreenTexts.DONE, (buttonWidget) -> {
					onClose();
				}));
	}

	@Override
	public void onClose() {
		this.properties.set("captureWidth", captureWidth.getText());
		this.properties.set("captureHeight", captureHeight.getText());
		this.properties.set("notifyDev", notifyDev.isChecked() ? "true" : "false");
		this.properties.set("notifyIncompatible", notifyIncompatible.isChecked() ? "true" : "false");
		this.properties.storeProperties();
		super.onClose();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.optionsListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private class OptionsListWidget extends ElementListWidget<OptionsListWidget.Entry> {
		private OptionsListWidget() {
			super(MineshotConfigScreen.this.client, MineshotConfigScreen.this.width,
					MineshotConfigScreen.this.height, 43, MineshotConfigScreen.this.height - 34,
					24);

			captureWidth = new TextFieldWidget(MineshotConfigScreen.this.textRenderer, 0, 0, 50, 20,
					new TranslatableText("mineshotrevived.config.width"));
			captureHeight = new TextFieldWidget(MineshotConfigScreen.this.textRenderer, 0, 0, 50,
					20, new TranslatableText("mineshotrevived.config.height"));
			notifyDev = new CheckboxWidget(0, 0, 20, 20,
					new TranslatableText("mineshotrevived.config.notify_dev"), false, false);
			notifyIncompatible = new CheckboxWidget(0, 0, 20, 20,
					new TranslatableText("mineshotrevived.config.notify_incompatible"), false, false);

			captureWidth.setText(properties.get("captureWidth"));
			captureHeight.setText(properties.get("captureHeight"));
			if (properties.get("notifyDev").equalsIgnoreCase("true"))
				notifyDev.onPress();
			if (properties.get("notifyIncompatible").equalsIgnoreCase("true"))
				notifyIncompatible.onPress();

			this.addEntry(new OptionListEntry(captureWidth));
			this.addEntry(new OptionListEntry(captureHeight));
			this.addEntry(new OptionListEntry(notifyDev));
			this.addEntry(new OptionListEntry(notifyIncompatible));
		}

		private class OptionListEntry extends OptionsListWidget.Entry {
			private ClickableWidget field;

			private OptionListEntry(ClickableWidget field) {
				this.field = field;
			}

			@Override
			public List<? extends Element> children() {
				return ImmutableList.of(this.field);
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth,
					int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				textRenderer.draw(matrices, field.getMessage(),
						MineshotConfigScreen.this.width / 2 - 150, y + 7, 0xFFFFFF);
				field.x = MineshotConfigScreen.this.width / 2;
				field.y = y;
				field.render(matrices, mouseX, mouseY, tickDelta);
			}

			public List<? extends Selectable> method_37025() {
				return Lists.newArrayList();
			}
		}

		private abstract class Entry extends ElementListWidget.Entry<OptionsListWidget.Entry> {
		}
	}
}
