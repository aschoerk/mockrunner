package com.mockrunner.tag;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.mockrunner.util.*;

public class NestedStandardTag extends TagSupport implements NestedTag
{
	private TagSupport tag;
	private PageContext pageContext;
	private Map attributes;
	private List childs;
	private boolean doRelease;
	
	public NestedStandardTag(TagSupport tag, PageContext pageContext)
	{
		this(tag, pageContext, new HashMap());
	}
	
	public NestedStandardTag(TagSupport tag, PageContext pageContext, Map attributes)
	{
		this.tag = tag;
		this.pageContext = pageContext;
		tag.setPageContext(pageContext);
		childs = new ArrayList();
		this.attributes = attributes;
		doRelease = false;
	}
	
	public void setDoRelease(boolean doRelease)
	{
		this.doRelease = doRelease;
	}
    
    public void populateAttributes()
    {
        TagUtil.populateTag(tag, attributes, doRelease);
    }
	
	public int doLifecycle() throws JspException
	{
        populateAttributes();
		int result = tag.doStartTag();
		if(Tag.EVAL_BODY_INCLUDE == result)
		{
			TagUtil.evalBody(childs, pageContext);
			while(IterationTag.EVAL_BODY_AGAIN == doAfterBody())
			{
				TagUtil.evalBody(childs, pageContext);
			}
		}
		return tag.doEndTag();
	}
	
	public TagSupport getTag()
	{
		return tag;
	}
	
	public void removeChilds()
	{
		childs.clear();
	}
	
	public List getChilds()
	{
		return childs;
	}
	
	public Object getChild(int index)
	{
		return childs.get(index);
	}
	
	public void addTextChild(String text)
	{
		if(null == text) text = "";
		childs.add(text);
	}
	
	public NestedTag addTagChild(Class tag)
	{
		return addTagChild(tag, new HashMap());
	}
	
	public NestedTag addTagChild(Class tag, Map attributeMap)
	{
		TagSupport tagSupport = TagUtil.createNestedTagInstance(tag, this.pageContext, attributeMap);	
		tagSupport.setParent(this.tag);
		childs.add(tagSupport);
        return (NestedTag)tagSupport;
	}
	
	public int doAfterBody() throws JspException
	{
		return tag.doAfterBody();
	}

	public int doEndTag() throws JspException
	{
		return tag.doEndTag();
	}

	public int doStartTag() throws JspException
	{
		return tag.doStartTag();
	}

	public String getId()
	{
		return tag.getId();
	}

	public Tag getParent()
	{
		return tag.getParent();
	}

	public Object getValue(String arg0)
	{
		return tag.getValue(arg0);
	}

	public Enumeration getValues()
	{
		return tag.getValues();
	}

	public void release()
	{
		tag.release();
	}

	public void removeValue(String arg0)
	{
		tag.removeValue(arg0);
	}

	public void setId(String arg0)
	{
		tag.setId(arg0);
	}

	public void setPageContext(PageContext pageContext)
	{
		this.pageContext = pageContext;
		tag.setPageContext(pageContext);
		for(int ii = 0; ii < childs.size(); ii++)
		{
			Object child = childs.get(ii);
            if(child instanceof TagSupport)
            {
                ((TagSupport)child).setPageContext(pageContext);
            }
		}
	}

	public void setParent(Tag arg0)
	{
		tag.setParent(arg0);
	}

	public void setValue(String arg0, Object arg1)
	{
		tag.setValue(arg0, arg1);
	}
    
    public String toString()
    {
        return TagUtil.dumpTag(this, new StringBuffer(), 0);
    }
}