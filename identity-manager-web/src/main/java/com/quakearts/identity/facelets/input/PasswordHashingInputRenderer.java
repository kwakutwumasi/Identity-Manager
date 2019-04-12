package com.quakearts.identity.facelets.input;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.quakearts.webapp.facelets.bootstrap.renderkit.Attribute;
import com.quakearts.webapp.facelets.bootstrap.renderkit.AttributeManager;
import com.quakearts.webapp.facelets.bootstrap.renderkit.html_basic.HtmlBasicInputRenderer;
import static com.quakearts.webapp.facelets.bootstrap.renderkit.RenderKitUtils.*;

public class PasswordHashingInputRenderer extends HtmlBasicInputRenderer {

	private static final String BUTTON = "button";
	private static final String AUTOCOMPLETE = "autocomplete";
	private static final String PLACEHOLDER = "placeholder";
	private static final String INPUT = "input";
	private static final String LABEL = "label";
	private static final String SPAN = "span_";
	private static final String CLASS = "class";
	private static final String STYLE = "style";
	private static final String STYLE_CLASS = "styleClass";
	public static final String RENDERER_TYPE = "com.quakearts.identity.input.renderer";
	private static final Attribute[] INPUT_ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);

	@Override
	protected void getEndTextToRender(FacesContext context, UIComponent component, String currentValue)
			throws IOException {

		if (!(component instanceof PasswordHashingComponent))
			throw new IOException("Component must be of type PasswordHashingComponent");

		PasswordHashingComponent hashComp = (PasswordHashingComponent) component;
		boolean validating = Boolean.parseBoolean(hashComp.get("validating"));

		ResponseWriter writer = context.getResponseWriter();

		String styleClass = hashComp.get(STYLE_CLASS);
		String style = hashComp.get(STYLE);

		String id = component.getClientId(context);

		if (validating) {
			writer.startElement("div", component);
			writeIdAttributeIfNecessary(context, writer, component);
		}

		writer.startElement("div", component);
		if (!validating)
			writeIdAttributeIfNecessary(context, writer, component);

		if (style != null)
			writer.writeAttribute(STYLE, style, null);
		writer.writeAttribute(CLASS, "input-group", null);
		writer.write("\n");
		writer.startElement("span", component);
		writer.writeAttribute(CLASS, "input-group-addon", null);
		writer.writeAttribute("id", SPAN + id, null);
		UIComponent labelFacet = component.getFacet(LABEL);
		if (labelFacet != null) {
			labelFacet.encodeAll(context);
		} else {
			String label = hashComp.get(LABEL);
			writer.write(label != null ? label : "Password");
		}

		writer.endElement("span");
		writer.write("\n");

		writer.startElement(INPUT, component);
		writer.writeAttribute("id", id + "_main", null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute(CLASS, "form-control" + (styleClass != null ? " " + styleClass : ""), STYLE_CLASS);
		if (validating) {
			writer.writeAttribute("onblur", "$('#" + id + "_validate').focus();", null);
		}

		renderPassThruAttributes(context, writer, component, INPUT_ATTRIBUTES);
		writer.writeAttribute("type", "password", null);
		writer.writeAttribute("value", "", null);
		writer.writeAttribute("aria-describedby", SPAN + id, null);
		String placeholder = hashComp.get(PLACEHOLDER);
		if (placeholder != null) {
			writer.writeAttribute(PLACEHOLDER, placeholder, null);
		} else {
			writer.writeAttribute(PLACEHOLDER, "Enter password", null);
		}

		writer.writeAttribute(AUTOCOMPLETE, "off", AUTOCOMPLETE);
		writer.endElement(INPUT);
		writer.write("\n");
		writer.endElement("div");
		writer.write("\n");

		addValidatorComponentIfNeccessary(context, component, hashComp, validating,
				writer, styleClass, id, labelFacet);
	}

	private void addValidatorComponentIfNeccessary(FacesContext context, UIComponent component,
			PasswordHashingComponent hashComp, boolean validating, ResponseWriter writer, String styleClass,
			String id, UIComponent labelFacet) throws IOException {
		String placeholder;
		if (validating) {
			writer.startElement("div", component);
			String validatorStyle = hashComp.get("validatorStyle");
			if(validatorStyle!=null){
				writer.writeAttribute(STYLE, validatorStyle, null);
			}
			writer.writeAttribute(CLASS, "input-group", null);
			writer.write("\n");
			writer.startElement("span", component);
			writer.writeAttribute(CLASS, "input-group-addon", null);
			writer.writeAttribute("id", SPAN + id, null);

			if (labelFacet != null) {
				labelFacet.encodeAll(context);
			} else {
				String label = hashComp.get(LABEL);
				writer.write(label != null ? label : "");
			}

			writer.endElement("span");
			writer.write("\n");

			writer.startElement(INPUT, component);
			writer.writeAttribute(CLASS, "form-control" + (styleClass != null ? " " + styleClass : ""), STYLE_CLASS);
			writer.writeAttribute("id", id + "_validate", null);
			writer.writeAttribute("name", id + "_validate", null);
			writer.writeAttribute("onblur",
					"javascript:" + "if($('#" + id + "_main').val() != $('#" + id + "_validate').val()){"
							+"$('#" + id + "_main').val(''); $('#" + id + "_validate').val('');"
							+ "$('#" + id + "_alert').removeClass('collapse');"+ "} else {$('#" 
							+ id + "_alert').addClass('collapse');};", null);
			renderPassThruAttributes(context, writer, component, INPUT_ATTRIBUTES);
			writer.writeAttribute("type", "password", null);
			writer.writeAttribute("value", "", null);
			writer.writeAttribute("aria-describedby", SPAN + id, null);

			placeholder = hashComp.get("validatorPlaceholder");
			if (placeholder != null) {
				writer.writeAttribute(PLACEHOLDER, placeholder, null);
			} else {
				writer.writeAttribute(PLACEHOLDER, "Confirm password", null);
			}

			writer.writeAttribute(AUTOCOMPLETE, "off", AUTOCOMPLETE);
			writer.write("\n");
			writer.endElement("div");
			writer.write("\n");
			addAlert(component, hashComp, id, writer);
			writer.endElement("div");
			writer.write("\n");
		}
	}

	private void addAlert(UIComponent component, PasswordHashingComponent hashComp, String id, ResponseWriter writer)
			throws IOException {
		writer.startElement("div", component);
		writer.writeAttribute("id", id+"_alert", null);		
		String alertStyle = hashComp.get("alertStyle");
		if (alertStyle != null) {
			writer.writeAttribute(STYLE, alertStyle, STYLE);
		}
		String alertClass = hashComp.get("alertClass");		
		writer.writeAttribute(CLASS, "alert alert-danger collapse "+(alertClass!=null?" "+alertClass:""), null);
		writer.writeAttribute("role", "alert", null);

		writer.write("\n");
		writer.writeText("Passwords do not match", component, null);
		writer.write("\n");
		
		boolean dismissible = Boolean.parseBoolean(hashComp.get("alertDismissible"));
		
		if(dismissible){
			writer.write("\n");
			writer.startElement(BUTTON, component);
			writer.writeAttribute("type", BUTTON, null);
			writer.writeAttribute(CLASS, "close", null);
			writer.writeAttribute("data-dismiss", "alert", null);
			writer.writeAttribute("aria-label", "Close", null);
			writer.startElement("span", component);
			writer.writeAttribute("aria-hidden", "true", null);
			writer.write("&times;");
			writer.endElement("span");
			writer.write("\n");
			writer.endElement(BUTTON);
			writer.write("\n");
		}
		
		writer.endElement("div");
		writer.write("\n");
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (context == null) {
			throw new NullPointerException("Context is null");
		}
		if (component == null) {
			throw new NullPointerException("Component is null");
		}
	}

}
