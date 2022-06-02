package nl.pascalroeleven.minecraft.mineshotrevived.client.config;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

public class MineshotConfigScreen extends GameOptionsScreen {
	private PropertiesHandler properties = Mineshot.getPropertiesHandler();
	private OptionsListWidget optionsListWidget;

	protected MineshotConfigScreen(Screen parent, GameOptions options) {
		super(parent, options, Text.translatable("mineshotrevived.config.title"));
	}

	@Override
	protected void init() {
		this.optionsListWidget = new OptionsListWidget();
		this.addSelectableChild(this.optionsListWidget);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE,
				(buttonWidget) -> {
					close();
				}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20,
				Text.translatable("mineshotrevived.config.reset"), (buttonWidget) -> {
					this.properties.forceDefaults();
					for (OptionsListWidget.Entry i : optionsListWidget.children()) {
						i.setValue(properties.get(i.propertyName));
					}
				}));
	}

	@Override
	public void close() {
		for (OptionsListWidget.Entry i : optionsListWidget.children()) {
			this.properties.set(i.propertyName, i.getValue());
		}

		this.properties.storeProperties();
		Mineshot.getUpdater().checkVersion();
		super.close();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.optionsListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private class OptionsListWidget extends ElementListWidget<OptionsListWidget.Entry> {
		private OptionsListWidget() {
			super(MineshotConfigScreen.this.client, MineshotConfigScreen.this.width, MineshotConfigScreen.this.height,
					43, MineshotConfigScreen.this.height - 34, 24);

			this.addEntry(new TextOptionEntry("captureWidth", "mineshotrevived.config.width"));
			this.addEntry(new TextOptionEntry("captureHeight", "mineshotrevived.config.height"));
			this.addEntry(new CheckboxOptionEntry("autoHideHUD", "mineshotrevived.config.auto_hide_hud"));
			this.addEntry(new TextOptionEntry("xRotation", "mineshotrevived.config.xrotation"));
			this.addEntry(new TextOptionEntry("yRotation", "mineshotrevived.config.yrotation"));
			this.addEntry(new CheckboxOptionEntry("notifyDev", "mineshotrevived.config.notify_dev"));
			this.addEntry(new CheckboxOptionEntry("notifyIncompatible", "mineshotrevived.config.notify_incompatible"));

			for (Entry i : this.children()) {
				i.setValue(properties.get(i.propertyName));
			}
		}

		@Override
		protected int getScrollbarPositionX() {
			return super.getScrollbarPositionX() + 15;
		}

		@Override
		public int getRowWidth() {
			return super.getRowWidth() + 32;
		}

		private class TextOptionEntry extends OptionsListWidget.Entry {
			TextOptionEntry(String propertyName, String translatableText) {
				super(new TextFieldWidget(MineshotConfigScreen.this.textRenderer, 0, 0, 50, 20,
						Text.translatable(translatableText)), propertyName);
			}

			@Override
			String getValue() {
				return ((TextFieldWidget) widget).getText();
			}

			@Override
			void setValue(String value) {
				((TextFieldWidget) widget).setText(value);
			}

		}

		private class CheckboxOptionEntry extends OptionsListWidget.Entry {
			CheckboxOptionEntry(String propertyName, String translatableText) {
				super(new CheckboxWidget(0, 0, 20, 20, Text.translatable(translatableText), false, false),
						propertyName);
			}

			@Override
			String getValue() {
				return ((CheckboxWidget) widget).isChecked() ? "true" : "false";
			}

			@Override
			void setValue(String value) {
				if (value.equalsIgnoreCase("true")) {
					if (!((CheckboxWidget) widget).isChecked())
						((CheckboxWidget) widget).onPress();
				} else {
					if (((CheckboxWidget) widget).isChecked())
						((CheckboxWidget) widget).onPress();
				}
			}

		}

		private abstract class Entry extends ElementListWidget.Entry<OptionsListWidget.Entry> {
			private static final int MARGIN = 125;
			private String propertyName;
			protected ClickableWidget widget;

			abstract String getValue();

			abstract void setValue(String value);

			private Entry(ClickableWidget field, String propertyName) {
				this.widget = field;
				this.propertyName = propertyName;
			}

			@Override
			public List<? extends Element> children() {
				return ImmutableList.of(this.widget);
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight,
					int mouseX, int mouseY, boolean hovered, float tickDelta) {
				textRenderer.draw(matrices, widget.getMessage(), MineshotConfigScreen.this.width / 2 - MARGIN, y + 7,
						0xFFFFFF);
				widget.x = MineshotConfigScreen.this.width / 2 + MARGIN - 50;
				widget.y = y;
				widget.render(matrices, mouseX, mouseY, tickDelta);
			}

			@Override
			public List<? extends Selectable> selectableChildren() {
				return ImmutableList.of(this.widget);
			}
		}

	}
}
