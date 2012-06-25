/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2012 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo.gui;

//import javax.media.opengl.*;

import igeo.*;

/**
   Graphic subobject class to draw a curve object by OpenGL
   
   @author Satoru Sugihara
   @version 0.7.0.0;
*/
public class ICurveGraphicGL extends IGraphicObject{
    public /*static*/ float weight = IConfig.strokeWeight; //1f;
    
    public ICurveI curve; // parent
    
    //public IGLLineStrip polyline;
    public IVec[] pts;
    
    public ICurveGraphicGL(ICurve crv){
	super(crv);
	//curve = crv.curve;
	//init();
    }
    
    public ICurveGraphicGL(ICurveR crv){
	super(crv);
	//curve = crv.curve;
	//init();
    }
    
    public void initCurve(){
	if(curve==null){ // added in 2011/10/18
	    if(parent instanceof ICurve){ curve = ((ICurve)parent).curve; }
	    else if(parent instanceof ICurveR){ curve = ((ICurveR)parent).curve; }
	}
	
	//IVec[] pts=null;
	
	if(curve.deg()==1){
	    int num = curve.num();
	    if(pts==null || pts.length!=num){ pts=new IVec[num]; }
	    for(int i=0; i<num; i++) pts[i] = curve.cp(i).get(); // how about when ustar!=0||uend!=1 ?
	    
	    //if(polyline!=null && polyline.pts!=null && polyline.pts.length==num){ pts = polyline.pts; }
	    //else{ pts = new IVec[num]; }
	    //for(int i=0; i<num; i++) pts[i] = curve.cp(i).get();
	}
	else{
	    int reso = IConfig.segmentResolution;
	    int epnum = curve.epNum() ;
	    int num = (epnum-1)*reso+1;
	    if(pts==null || pts.length!=num){ pts=new IVec[num]; }
	    //if(polyline!=null && polyline.pts!=null && polyline.pts.length==num){ pts = polyline.pts; }
	    //else{ pts = new IVec[num]; }
	    for(int i=0; i<epnum; i++){
		for(int j=0; j<reso; j++){
		    if(i<epnum-1 || j==0){
			pts[i*reso + j] = curve.pt(curve.u(i,(double)j/reso)).get();
		    }
		}
	    }
	}
	
	//if(polyline==null || polyline.pts != pts){ polyline = new IGLLineStrip(pts); }
	
	if(update) update=false;
    }
    
    public void setWeight(float w){ weight=w; }
    public float getWeight(){ return weight; }
    
    public boolean isDrawable(IGraphicMode m){
	//return m.isGL();
	return m.isGraphic3D();
    }
    
    public void draw(IGraphics g){
	
	if(curve==null || update /*&& curve.deg()>1*/ ) // now need to be updated with deg 1
	    initCurve(); // not initizlized at the constructor // shouldn't it?
	
	
	if(g.type() == IGraphicMode.GraphicType.GL ||
	   g.type() == IGraphicMode.GraphicType.P3D ){
	    
	    IGraphics3D g3d = (IGraphics3D)g;
	    
	    g3d.weight(weight);
	    
	    float red,green,blue,alpha;
	    if(color!=null){
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();
		alpha = color.getAlpha();
	    }
	    else{
		red = IConfig.objectColor.getRed();
		green = IConfig.objectColor.getGreen();
		blue = IConfig.objectColor.getBlue();
		alpha = IConfig.objectColor.getAlpha();
	    }
	    
	    if(g3d.view().mode().isTransparent()&&g3d.view().mode().isTransparentWireframe())
		alpha = IConfig.transparentModeAlpha;
	    
	    if(g3d.view().mode().isLight()&&g3d.view().mode().isLightWireframe()){
		g3d.ambient(red,green,blue,alpha);
		g3d.diffuse(red,green,blue,alpha);
		g3d.shininess(IConfig.shininess);
		g3d.stroke(red,green,blue,0f);
	    }
	    //else{ g3d.stroke(red,green,blue,alpha); }
	    
	    if(g3d.view().mode().isLight()&&!g3d.view().mode().isLightWireframe())
		g3d.disableLight();
	    
	    g3d.stroke(red,green,blue,alpha);
	    
	    //polyline.draw(gl);
	    //g3d.drawLineStrip(polyline.pts);
	    if(pts.length==2){
		//g3d.drawLines(pts);
		g3d.drawLineStrip(pts);
	    }
	    else{ g3d.drawLineStrip(pts); }
	    
	    
	    //gl.glBegin(GL.GL_LINE_STRIP);
	    //for(int i=0; i<pts.length; i++){
	    //gl.glVertex3d(pts[i].x, pts[i].y, pts[i].z);
	    //gl.glVertex3f((float)pts[i].x, (float)pts[i].y, (float)pts[i].z);
	    //}
	    //gl.glEnd();
	    
	    if(g3d.view().mode().isLight()&&!g3d.view().mode().isLightWireframe())
		g3d.enableLight();
	    

	    /*

	    GL gl = ((IGraphicsGL)g).getGL();
	    //GL gl = g.getGL();
	    
	    if(gl!=null){
		gl.glLineWidth(weight);
		//gl.glLineStipple(0,(short)0xFFFF);

		float red,green,blue,alpha;
		//float red = defaultRed;
		//float green = defaultGreen;
		//float blue = defaultBlue;
		//float alpha = defaultAlpha;
		if(color!=null){
		    red = (float)color.getRed()/255;
		    green = (float)color.getGreen()/255;
		    blue = (float)color.getBlue()/255;
		    alpha = (float)color.getAlpha()/255;
		}
		else{
		    red = (float)IConfig.objectColor.getRed()/255;
		    green = (float)IConfig.objectColor.getGreen()/255;
		    blue = (float)IConfig.objectColor.getBlue()/255;
		    alpha = (float)IConfig.objectColor.getAlpha()/255;
		}
		
		if(g.view().mode().isTransparent()&&g.view().mode().isTransparentWireframe())
		    alpha = (float)IConfig.transparentModeAlpha/255;
		
		if(g.view().mode().isLight()&&g.view().mode().isLightWireframe()){
		    float[] colorf = new float[]{ red, green, blue, alpha };
		    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, colorf, 0);
		    gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, colorf, 0);
		    //gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, colorf, 0);
		    gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,
				   ISurfaceGraphicGL.defaultShininess);
		    gl.glColor4f(red, green, blue, 0f); // ? without this, the color is tinted with the previous object's color
		}
		else{ gl.glColor4f(red, green, blue, alpha); }
		
		if(g.view().mode().isLight()&&!g.view().mode().isLightWireframe())
		    gl.glDisable(GL.GL_LIGHTING);
		
		gl.glColor4f(red, green, blue, alpha);
		polyline.draw(gl);
		//gl.glBegin(GL.GL_LINE_STRIP);
		//for(int i=0; i<pts.length; i++){
		//gl.glVertex3d(pts[i].x, pts[i].y, pts[i].z);
		//gl.glVertex3f((float)pts[i].x, (float)pts[i].y, (float)pts[i].z);
		//}
		//gl.glEnd();
		
		if(g.view().mode().isLight()&&!g.view().mode().isLightWireframe())
		    gl.glEnable(GL.GL_LIGHTING);
	    }
	    */
	}
    }
    
}
