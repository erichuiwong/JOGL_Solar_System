package a2;

import graphicslib3D.*;
import graphicslib3D.shape.*;

import graphicslib3D.GLSLUtils.*;
import java.io.*;
import java.nio.*;
import javax.swing.*;
import java.awt.event.*;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.texture.*;

public class Starter extends JFrame implements GLEventListener, KeyListener
{	private GLCanvas myCanvas;
	private int rendering_program;
	private int axes_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[4];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float axisX, axisY, axisZ;
	private float sphLocX, sphLocY, sphLocZ;
	private GLSLUtils util = new GLSLUtils();
	private	MatrixStack mvStack = new MatrixStack(20);
	private Sphere mySphere = new Sphere(24);
	private Boolean axesFlag = true;

	private FloatBuffer axesColor = FloatBuffer.allocate(4);

	private int sunTexture;
	private Texture joglSunTexture;

	private int earthTexture;
	private Texture joglEarthTexture;

	private int moonTexture;
	private Texture joglMoonTexture;

	private int brownTexture;
	private Texture joglBrownTexture;

	private int iceTexture;
	private Texture joglIceTexture;

	public Starter()
	{	setTitle("Assignment #2");
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addKeyListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();
	}

	public void keyPressed(KeyEvent e) {
		//Grabs Key Pressed by User
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W) {
			cameraZ -= 0.25f;
		}
		if (key == KeyEvent.VK_S) {
			cameraZ += 0.25f;
		}
		if (key == KeyEvent.VK_A) {
			cameraX -= 0.25f;
		}
		if (key == KeyEvent.VK_D) {
			cameraX += 0.25f;
		}
		if (key == KeyEvent.VK_E) {
			cameraY += 0.25f;	
		}
		if (key == KeyEvent.VK_Q) {
			cameraY -= 0.25f;
		}
		if (key == KeyEvent.VK_LEFT) {
			mvStack.rotate(5.0f, 0.0f, cameraY-5.0f, 0.0f);
		}
		if (key == KeyEvent.VK_RIGHT) {
			mvStack.rotate(5.0f, 0.0f, cameraY+5.0f, 0.0f);
		}
		if (key == KeyEvent.VK_UP) {
			mvStack.rotate(5.0f, cameraX-5.0f, 0.0f, 0.0f);
		}
		if (key == KeyEvent.VK_DOWN) {
			mvStack.rotate(5.0f, cameraX+5.0f, 0.0f, 0.0f);
		}
		if (key == KeyEvent.VK_SPACE) {
			axesFlag = !axesFlag;
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) drawable.getGL();
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

		float aspect = myCanvas.getWidth() / myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);

		// push view matrix onto the stack
		mvStack.pushMatrix();
		mvStack.translate(-cameraX, -cameraY, -cameraZ);
		double amt = (double)(System.currentTimeMillis())/1000.0;

		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		
		// -----------------------SUN---------------------------------------//
		mvStack.pushMatrix();
		mvStack.translate(sphLocX, sphLocY, sphLocZ);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		//Builds Sphere
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		//Texture
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
		//All object to pass behind
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);

		int numVerts = mySphere.getIndices().length;
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts); 
		mvStack.popMatrix();
		
		// -----------------------Planet 1 and Moon------------------------//
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/5.0,0.0,5.0,0.0);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();
		//--Moon--//	
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.sin(0.5*amt)*3.0f, Math.cos(0.5*amt)*3.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,3.0,0.0);
		mvStack.scale(0.25, 0.25, 0.25);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);

		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();  mvStack.popMatrix(); 

		// ------------------------Planet 2 and Moons------------------------//
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(0.5*amt)*6.0f, 0.0f, Math.cos(0.5*amt)*6.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,9.0,0.0);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);	

		gl.glBindTexture(GL_TEXTURE_2D, iceTexture);

		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix();
		//--Moon 1--//	
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,8.0,0.0);
		mvStack.scale(0.25, 0.25, 0.25);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindTexture(GL_TEXTURE_2D, brownTexture);

		gl.glDrawArrays(GL_TRIANGLES, 0, numVerts);
		mvStack.popMatrix(); 
		//--Moon 2 (Custom Model)--//
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.sin(amt)*4.0f, Math.cos(amt)*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,8.0,0.0);
		mvStack.scale(0.25, 0.25, 0.25);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindTexture(GL_TEXTURE_2D, brownTexture);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		mvStack.popMatrix();
		mvStack.popMatrix(); //Pop off Planet 2
		mvStack.popMatrix(); //Pop off Sun

		if (axesFlag) {
			gl.glUseProgram(axes_program);
			gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
			mvStack.translate(axisX, axisY, axisZ);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			//Red Color
			axesColor.put(0, 1.0f); axesColor.put(1, 0.0f); axesColor.put(2, 0.0f); axesColor.put(3, 1.0f);
			gl.glUniform1fv(gl.glGetUniformLocation(axes_program, "axesColor"), 4, axesColor);
			gl.glDrawArrays(GL_LINES, 0, 2);
			//Green Color
			axesColor.put(0, 0.0f); axesColor.put(1, 1.0f); axesColor.put(2, 0.0f); axesColor.put(3, 1.0f);
			gl.glUniform1fv(gl.glGetUniformLocation(axes_program, "axesColor"), 4, axesColor);
			gl.glDrawArrays(GL_LINES, 2, 3);
			//Blue Color
			axesColor.put(0, 0.0f); axesColor.put(1, 0.0f); axesColor.put(2, 1.0f); axesColor.put(3, 1.0f);
			gl.glUniform1fv(gl.glGetUniformLocation(axes_program, "axesColor"), 4, axesColor);
			gl.glDrawArrays(GL_LINES, 4, 6);
		}

		mvStack.popMatrix(); //Pop off View
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) drawable.getGL();
		rendering_program = createShaderProgram();
		axes_program = createAxesProgram();
		setupVertices();
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
		sphLocX = 0.0f; sphLocY = 0.0f; sphLocZ = 0.0f;
		cubeLocX = 0.0f; cubeLocY = 0.0f; cubeLocZ = 0.0f;
		axisX = 0.0f; axisY = 0.0f; axisZ = 0.0f;

		joglSunTexture = loadTexture("sun.jpg");
		sunTexture = joglSunTexture.getTextureObject();

		joglEarthTexture = loadTexture("earth.jpg");
		earthTexture = joglEarthTexture.getTextureObject();

		joglMoonTexture = loadTexture("moon.jpg");
		moonTexture = joglMoonTexture.getTextureObject();

		joglBrownTexture = loadTexture("bkgd1.jpg");
		brownTexture = joglBrownTexture.getTextureObject();

		joglIceTexture = loadTexture("ice.jpg");
		iceTexture = joglIceTexture.getTextureObject();
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		float[] cube_positions =
		{	-1.0f,  5.0f, -1.0f, -1.0f, -8.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -8.0f, 1.0f,  5.0f, -1.0f, -1.0f,  5.0f, -1.0f,
			5.0f, -1.0f, -1.0f, 5.0f, -1.0f,  8.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  5.0f, 1.0f,  1.0f,  8.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  8.0f, -1.0f, -5.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -5.0f,  1.0f,  1.0f, 1.0f,  8.0f,  1.0f,
			-1.0f, -8.0f,  1.0f, -1.0f, -1.0f, -1.0f, -5.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -6.0f,  1.0f, -1.0f, -8.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  8.0f,  1.0f, -1.0f,  5.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -8.0f, -1.0f, -1.0f, -9.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  9.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  8.0f, -1.0f,  1.0f,  1.0f, -3.0f,  1.0f, -1.0f
		};

		Vertex3D[] vertices = mySphere.getVertices();
		int[] indices = mySphere.getIndices();

		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];

		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vertices[indices[i]]).getX();
			pvalues[i*3+1] = (float) (vertices[indices[i]]).getY();
			pvalues[i*3+2] = (float) (vertices[indices[i]]).getZ();
			tvalues[i*2] = (float) (vertices[indices[i]]).getS();
			tvalues[i*2+1] = (float) (vertices[indices[i]]).getT();
			nvalues[i*3] = (float) (vertices[indices[i]]).getNormalX();
			nvalues[i*3+1]= (float)(vertices[indices[i]]).getNormalY();
			nvalues[i*3+2]=(float) (vertices[indices[i]]).getNormalZ();
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cube_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);
	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		return r;
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("a2/vert.shader");
		String fshaderSource[] = util.readShaderSource("a2/frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	private int createAxesProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("a2/axes.shader");
		String fshaderSource[] = util.readShaderSource("a2/axes.frag");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	public Texture loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tex;
	}
}