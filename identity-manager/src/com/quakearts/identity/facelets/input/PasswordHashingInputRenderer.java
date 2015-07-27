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

	public static final String RENDERER_TYPE = "com.quakearts.identity.input.renderer";
    private static final Attribute[] INPUT_ATTRIBUTES =
            AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);

	@Override
	protected void getEndTextToRender(FacesContext context,
			UIComponent component, String currentValue) throws IOException {
				
		if(! (component instanceof PasswordHashingComponent))
			throw new IOException("Component must be of type PasswordHashingComponent");
		
		PasswordHashingComponent hashComp = (PasswordHashingComponent)component;
		boolean validating = Boolean.parseBoolean(hashComp.get("validating"));
		
        ResponseWriter writer = context.getResponseWriter();
        
        String styleClass = hashComp.get("styleClass");
		String style = hashComp.get("style");
        
        String id = component.getClientId(context);
        String idJs = id.replace(":", "\\\\:");
        
        if(validating){
            writer.startElement("div", component);
            writeIdAttributeIfNecessary(context, writer, component);
        }
        
        writer.startElement("div", component);
        if(!validating)
        	writeIdAttributeIfNecessary(context, writer, component);
 
        if(style!=null)
        	writer.writeAttribute("stlye", style, null);        
        writer.writeAttribute("class", "input-group", null);
		writer.write("\n");
        writer.startElement("span", component);
        writer.writeAttribute("class", "input-group-addon", null);
        writer.writeAttribute("id", "span_"+id, null);
        UIComponent labelFacet = component.getFacet("label");
        if(labelFacet!=null){
        	labelFacet.encodeAll(context);
        } else {
    		String label = hashComp.get("label");
    		writer.write(label!=null?label:"Password");
        }
        
        writer.endElement("span");
		writer.write("\n");
		
		writer.startElement("input", component);
        writer.writeAttribute("id", id+"_main", null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("class", "form-control"+(styleClass!=null?" "+styleClass:""), "styleClass");
        if(validating){
        	writer.writeAttribute("onblur", "$('#"+idJs+"_validate').focus();",null);
        }
        
    	renderPassThruAttributes(context, writer, component, INPUT_ATTRIBUTES);
        writer.writeAttribute("type", "password", null);
        writer.writeAttribute("value", "", null);        
        writer.writeAttribute("aria-describedby", "span_"+id, null);
		String placeholder = hashComp.get("placeholder");
        if( placeholder!=null){
        	writer.writeAttribute("placeholder", placeholder, null);
        }
        
        writer.writeAttribute("autocomplete","off","autocomplete");
        writer.endElement("input");        
		writer.write("\n");
        writer.endElement("div");
		writer.write("\n");
        
        if(validating){            
            writer.startElement("div", component);
            
            if(style!=null)
            	writer.writeAttribute("stlye", style, null);
            
            writer.writeAttribute("class", "input-group", null);
    		writer.write("\n");
            writer.startElement("span", component);
            writer.writeAttribute("class", "input-group-addon", null);
            writer.writeAttribute("id", "span_"+id, null);

            if(labelFacet!=null){
            	labelFacet.encodeAll(context);
            } else {
        		String label = hashComp.get("label");
        		writer.write(label!=null?label:"Confirm Password");
            }
            
            writer.endElement("span");
    		writer.write("\n");
            
	        writer.startElement("input", component);
	        writer.writeAttribute("class", "form-control"+(styleClass!=null?" "+styleClass:""), "styleClass");
	        writer.writeAttribute("id", id+"_validate", null);
	        writer.writeAttribute("name", id+"_validate", null);
	        writer.writeAttribute("onblur", "javascript:" +
	        	"if($('#"+id+"_main').val() != $('#"+idJs+"_validate').val()){" +
	        			"alert('Passwords do not match');" +
	        			"$('#"+id+"_main').focus();" +
	        			"};",null);
	    	renderPassThruAttributes(context, writer, component, INPUT_ATTRIBUTES);
	        writer.writeAttribute("type", "password", null);
	        writer.writeAttribute("value", "", null);
	        writer.writeAttribute("aria-describedby", "span_"+id, null);

	        if( placeholder!=null){
	        	writer.writeAttribute("placeholder", placeholder, null);
	        }
	        
	        writer.writeAttribute("autocomplete","off","autocomplete");
	        writer.write("\n");
	        writer.endElement("div");
			writer.write("\n");
	        writer.endElement("div");
			writer.write("\n");
        }
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if(context == null)
		{
			throw new NullPointerException("Context is null");
		}
		if(component == null)
		{
			throw new NullPointerException("Component is null");
		} else
		{
			return;
		}
	}

	
}
