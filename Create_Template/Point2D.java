// impliment as plugin
 
public class Point2D {
	public float x, y, value;
	
	public Point2D() {
		clear();
	}
	public Point2D(Point2D origpt) {
		Copy(origpt);
	}
	public Point2D(float xx, float yy, float val) {
		x = xx;
		y = yy;
		value = val;		
	}
	public void clear() {
		x = 0.0f;
		y = 0.0f;
		value = -1.0f;
	}		
	public void Copy(Point2D origpt) {
		x = origpt.x;
		y = origpt.y;
		value = origpt.value;
	}
	public double length() {
		return(sqrt(x*x + y*y));
	}		
	public float sep(Point2D origpt) {
		return(sqrt((x-origpt.x)*(x-origpt.x) + 
					(y-origpt.y)*(y-origpt.y)));
	}		
	public String toString() {
		return (" "+x+", "+y+"  val "+value);
	}
        
        // generic math routines
        double sqrt( double x ) {
           return(Math.sqrt(x));
        }
        float sqrt( float x ) {
           return (float)(Math.sqrt((double)x));
        }

}
