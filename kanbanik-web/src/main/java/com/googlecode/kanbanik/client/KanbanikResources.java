package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface KanbanikResources extends ClientBundle {
	
	public static final KanbanikResources INSTANCE =  GWT.create(KanbanikResources.class);

    @Source("images/maximize.png")
    ImageResource maximize();

    @Source("images/minimize.png")
    ImageResource minimize();

	@Source("images/edit.png")
	ImageResource editButtonImage();
	
	@Source("images/editDisabled.png")
	ImageResource editButtonDisabledImage();
	
	@Source("images/delete.png")
	ImageResource deleteButtonImage();
	
	@Source("images/deleteDisabled.png")
	ImageResource deleteButtonDisabledImage();
	
	@Source("images/add.png")
	ImageResource addButtonImage();

	@Source("images/addDisabled.png")
	ImageResource addDisabledButtonImage();

	@Source("images/chain.png")
	ImageResource chainImage();

	@Source("images/arrowRight.png")
	ImageResource rightDropArrowImage();
	
	@Source("images/arrowDown.png")
	ImageResource downDropArrowImage();

	@Source("images/arrowInside.png")
	ImageResource insideDropArrowImage();
	
	@Source("images/progressbar.gif")
	ImageResource progressBarImage();
	
	@Source("images/noUserPicture.png")
	ImageResource noUserPicture();
	
	// Rich Text Editor Resources
	
	@Source("images/richTextEditor/bold.png")
	ImageResource bold();
	
	@Source("images/richTextEditor/italic.png")
	ImageResource italic();
	
	@Source("images/richTextEditor/underline.png")
	ImageResource underline();
	
	@Source("images/richTextEditor/stroke.png")
	ImageResource stroke();
	
	@Source("images/richTextEditor/subscript.png")
	ImageResource subscript();
	
	@Source("images/richTextEditor/superscript.png")
	ImageResource superscript();
	
	@Source("images/richTextEditor/alignleft.png")
	ImageResource alignleft();
	
	@Source("images/richTextEditor/alignmiddle.png")
	ImageResource alignmiddle();
	
	@Source("images/richTextEditor/alignright.png")
	ImageResource alignright();
	
	@Source("images/richTextEditor/orderlist.png")
	ImageResource orderlist();
	
	@Source("images/richTextEditor/unorderlist.png")
	ImageResource unorderlist();
	
	@Source("images/richTextEditor/indentright.png")
	ImageResource indentright();
	
	@Source("images/richTextEditor/indentleft.png")
	ImageResource indentleft();
	
	@Source("images/richTextEditor/generatelink.png")
	ImageResource generatelink();
	
	@Source("images/richTextEditor/breaklink.png")
	ImageResource breaklink();
	
	@Source("images/richTextEditor/insertline.png")
	ImageResource insertline();
	
	@Source("images/richTextEditor/insertimage.png")
	ImageResource insertimage();
	
	@Source("images/richTextEditor/removeformatting.png")
	ImageResource removeformatting();
	
	@Source("images/richTextEditor/texthtml.png")
	ImageResource texthtml();

	// CSS Resources
	@Source("style/Board.css")
	BoardStyle boardStyle();
}
