package geometry;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import util.Miniset;


public class Polygon2D {
	Vector2D[] vertices;
	
	public Polygon2D(Vector2D... vertices){
		this.vertices = vertices;
	}
	
	public Polygon2D(Vector2D center, double distance, int vertexAmount){
		vertices = new Vector2D[vertexAmount];
		for(int i = 0; i < vertexAmount; i++){
			vertices[i] = new Vector2D(Math.cos(Math.toRadians(360 / vertexAmount * i)) * distance + center.x,
					Math.sin(Math.toRadians(360 / vertexAmount * i)) * distance + center.y);
		}
	}
	
	public Polygon2D(Polygon2D copy){
		vertices = new Vector2D[copy.vertices.length];
		for(int i = 0; i < copy.vertices.length; i++){
			vertices[i] = new Vector2D(copy.vertices[i]);
		}
	}
	
	public Rectangle2D AABB(){
		double xMin = vertices[0].x;
		double xMax = vertices[0].x;
		double yMin = vertices[0].y;
		double yMax = vertices[0].y;
		for(int i = 1; i < vertices.length; i++){
			if(vertices[i].x < xMin){
				xMin = vertices[i].x;
			}
			if(vertices[i].x > xMax){
				xMax = vertices[i].x;
			}
			if(vertices[i].y < yMin){
				yMin = vertices[i].y;
			}
			if(vertices[i].y > yMax){
				yMax = vertices[i].y;
			}
		}
		return new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	public double getArea(){
		double ret = (vertices[vertices.length - 1].x * vertices[0].y) - (vertices[vertices.length - 1].y * vertices[0].x);
		for(int i = 0; i < vertices.length - 1; i++){
			ret += (vertices[i].x * vertices[i + 1].y) - (vertices[i].y * vertices[i + 1].x);
		}
		return Math.abs(ret / 2);
	}
	
	public void translate(Vector2D by){
		for(Vector2D i: vertices){
			i.set(i.add(by));
		}
	}

	public Polygon2D quickHull(){
		if(vertices.length < 3){
			return new Polygon2D(this);
		}
		Vector2D xMin = vertices[0];
		Vector2D xMax = vertices[0];
		for(int i = 1; i < vertices.length; i++){
			if(vertices[i].x < xMin.x){
				xMin = vertices[i];
			}
			if(vertices[i].x > xMax.x){
				xMax = vertices[i];
			}
		}
		//Vector2D perp = xMax.perp(xMin);
		ArrayList<Vector2D> hull = new ArrayList<Vector2D>(vertices.length);
		hull.add(xMax);
		hull.add(xMin);
		ArrayList<Vector2D> above = new ArrayList<Vector2D>(vertices.length / 2);
		ArrayList<Vector2D> below = new ArrayList<Vector2D>(vertices.length / 2);
		for(Vector2D i: vertices){
			byte val = i.side(xMax, xMin);
			if(val > 0){
				above.add(i);
			} else if(val < 0){
				below.add(i);
			}
		}
		quickHullRecursive(hull, above, xMax, xMin);
		quickHullRecursive(hull, below, xMin, xMax);
		return new Polygon2D(hull.toArray(new Vector2D[0]));
	}
	
	private void quickHullRecursive(ArrayList<Vector2D> hull, ArrayList<Vector2D> vectors, Vector2D a, Vector2D b){
		if(vectors.isEmpty())return;
		Vector2D perp = b.subtract(a).rightPerp();
		Vector2D farthest = vectors.get(0);
		double farthestDot = vectors.get(0).dot(perp);
		for(Vector2D i: vectors){
			if(i.dot(perp) > farthestDot){
				farthest = i;
				farthestDot = i.dot(perp);
			}
		}
		hull.add(Math.min(hull.indexOf(a), hull.indexOf(b) + 1), farthest);
		ArrayList<Vector2D> AF = new ArrayList<Vector2D>(vectors.size() / 3);
		ArrayList<Vector2D> FB = new ArrayList<Vector2D>(vectors.size() / 3);
		//System.out.println(farthest);
		for(Vector2D i: vectors){
			if(i.side(a, farthest) > 0){
				AF.add(i);
			}else if(i.side(farthest, b) > 0){
				FB.add(i);
			}
		}
		quickHullRecursive(hull, AF, a, farthest);
		quickHullRecursive(hull, FB, farthest, b);
	}

	public Polygon toPolygon(){
		int[] xPoints = new int[vertices.length];
		int[] yPoints = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++){
			xPoints[i] = (int) vertices[i].x;
			yPoints[i] = (int) vertices[i].y;
		}
		return new Polygon(xPoints, yPoints, vertices.length);
	}

	protected static Vector2D minkowskiSupport(Polygon2D a, Polygon2D b, Vector2D d){
		return getSupportPoint(a, d).subtract(getSupportPoint(b, d.negate()));
	}
	
	private static Vector2D getSupportPoint(Polygon2D from, Vector2D d){
		double highest = -Double.MAX_VALUE;
	    Vector2D support = new Vector2D(0, 0);

	    for (int i = 0; i < from.vertices.length; ++i) {
	        Vector2D v = from.vertices[i];
	        double dot = v.dot(d);
	        if (dot > highest) {
	            highest = dot;
	            support = v;
	        }
	    }
	    return support;
	}

	public static Vector2D intersection(Polygon2D aP, Polygon2D bP){
		if(aP == bP) return null;
		//if(aP.equals(bP))return true; //Commented this out because it still needs to figure out the intersection vector
		if(aP.vertices.length < 3 || bP.vertices.length < 3) return null;
		Polygon2D a = aP.quickHull();
		Polygon2D b = bP.quickHull();
		//if(a.equals(b) || aP.equals(b) || a.equals(bP))return true;
		//Random Direction
		Vector2D d = new Vector2D(1, 1);
		//Set of vertices
		Miniset<Vector2D> W = new Miniset<Vector2D>();
		//direction from A in a direction to B in the opposite direction. Using the opposite direction will hopefully
		//make the biggest difference.
		Vector2D s = minkowskiSupport(a, b, d);
		//Add that direction
		W.add(s);
		//the new direction is the negative support, hopefully making the simplex bigger
		d = s.negate();
		while(true){
			//direction from A in direction -s to b in direction s. Hopefully this will make a big simplex.
			Vector2D A = minkowskiSupport(a, b, d);
			//If A isn't past the origin in direction d, then it cant possibly contain the origin
			if(A.dot(d) < 0) return null; //No intersection
			//Add the new point to the simplex
			W.add(A);
			if(DoSimplex(W, d)){
				return EPA(W, a, b);
			}
		}
	}
	
	private static Vector2D EPA(Miniset<Vector2D> simplex, Polygon2D A, Polygon2D B){
		while(true){
			Edge e = findClosestEdge(simplex);
			Vector2D support = minkowskiSupport(A, B, e.normal);
			double d = support.dot(e.normal);
			//The next bit in the tutorial is a fancy way of figuring out if the support point is basically the same as the
			//two points of the edge
			if(d - e.distance < 0.00001){ //having a tolerance is only necessary for shapes with curves... i think
			//if(d == e.distance){
				return e.normal.multiply(new Vector2D(d, d));
			} else {
				simplex.add(support, e.index);
			}
		}
	}
	
	private static Edge findClosestEdge(Miniset<Vector2D> simplex){
		Edge closest = new Edge();
		closest.distance = Double.MAX_VALUE;
		for(int i = 0; i < simplex.size(); i++){
			int j = (i + 1 >= simplex.size()? 0 : i + 1);
			Vector2D a = simplex.get(i);
			Vector2D b = simplex.get(j);
			Vector2D ab = b.subtract(a);
			Vector2D abp;
			//This if statement is basically like triple product but it handles some problem that tripleproduct ignores
			if(!Vector2D.counterClockwise(simplex.get(0), simplex.get(1), simplex.get(2))){
				abp = ab.rightPerp();
			}else{
				abp = ab.leftPerp();
			}
			//abp = Vector2D.tripleProduct(ab, a, ab);
			abp.set(abp.normalize());
			double dot = abp.dot(a);
			if(dot < closest.distance){
				closest.distance = dot;
				closest.normal = abp;
				closest.index = j;
			}
		}
		return closest;
	}
	
	static boolean DoSimplex(Miniset<Vector2D> simplex, Vector2D d){
		switch(simplex.size()){
		case 2: return twoPoints(simplex, d);
		case 3: return threePoints(simplex, d);
		default: return false;
		}
	}
	
	private static boolean threePoints(Miniset<Vector2D> simplex, Vector2D d){
		Vector2D A = simplex.get(simplex.size() - 1);
		Vector2D B = simplex.get(simplex.size() - 2);
		Vector2D C = simplex.get(simplex.size() - 3);
		Vector2D AO = A.negate();
		Vector2D AB = B.subtract(A);
		Vector2D AC = C.subtract(A);
		//Vector2D ABC = AB.crossProduct(AC);
		Vector2D ACPerp = Vector2D.tripleProduct(AB, AC, AC);
		//Changing this from (AB, AB, AC) to (AC, AB, AB) fixed the problem
		Vector2D ABPerp = Vector2D.tripleProduct(AC, AB, AB);
		
		if(ABPerp.dot(AO) > 0){
			simplex.set(B, A);
			d.set(ABPerp);
			return false;
		} else if(ACPerp.dot(AO) > 0){
			simplex.set(C, A);
			d.set(ACPerp);
			return false;
		} else{
			return true;
		}
	}
	
	private static boolean twoPoints(Miniset<Vector2D> simplex, Vector2D d){
		Vector2D A = simplex.get(simplex.size() - 1);
		Vector2D B = simplex.get(simplex.size() - 2);
		//AO is the distance between A and the origin
		Vector2D AO = A.negate();
		//AB is the distance between A and B
		Vector2D AB = B.subtract(A);
		//This if basically is asking if the direction of the line AB is in the same direction as line AO
		//So it is essentially asking if AB is pointing towards the origin.
		if(AB.dot(AO) > 0){
			simplex.set(B, A);
			/*
			 * Set direction to perpendicular pointing ~towards the origin
			 */
			d.set(Vector2D.tripleProduct(AB, AO, AB));
		}else{
			simplex.set(A);
			d.set(AO);
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o instanceof Polygon2D){
			if(vertices.length != ((Polygon2D) o).vertices.length){
				return false;
			}
			outerLoop: for(Vector2D i: vertices){
				for(Vector2D j: ((Polygon2D) o).vertices){
					//System.out.println("testing: ");
					if(i.equals(j)){
						//System.out.println("equals!");
						continue outerLoop;
					}
					//System.out.println("not equals!");
				}
				//System.out.println("Returning false");
				//Will only ever get to this point if there is no Vector in o equal to vector i
				return false;
			}
			//Will only ever get to this point if all vertices in this match all vertices in o
			return true;
		}
//		else{
//			System.out.println(o.toString());
//		}
		return false;
	}

	@Override
	public String toString(){
		String ret = "";
		for(int i = 0; i < vertices.length; i++){
			ret += vertices[i].round().toString() + ", ";
		}
		return ret;
	}
	
	static class Edge{
		Vector2D normal;
		int index;
		double distance;
	}
}
