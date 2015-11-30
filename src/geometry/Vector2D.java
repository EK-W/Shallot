package geometry;


public class Vector2D {
	public double x;
	public double y;
	
	/**
	 * Creates a vector at the origin
	 */
	public Vector2D(){
		this(0, 0);
	}
	/**
	 * Creates a vector at the given coordinates
	 * @param x the x value of the vector
	 * @param y the y value of the vector
	 */
	public Vector2D(double x, double y){
		set(x, y);
	}

	/**
	 * Creates a vector with the same coordinates as the given vector
	 * @param copy the vector to copy
	 */
	public Vector2D(Vector2D copy){
		set(copy);
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void set(Vector2D to) {
		set(to.x, to.y);
	}
	
	public Vector2D add(Vector2D by){
		return new Vector2D(this.x + by.x, this.y + by.y);
	}
	public Vector2D subtract(Vector2D by){
		return new Vector2D(this.x - by.x, this.y - by.y);
	}
	public Vector2D multiply(Vector2D by){
		return new Vector2D(this.x * by.x, this.y * by.y);
	}
	public Vector2D divide(Vector2D by){
		return new Vector2D(doDivide(this.x, by.x), doDivide(this.y, by.y));
	}
	public Vector2D pow(Vector2D by){
		return new Vector2D(Math.pow(this.x, by.x), Math.pow(this.y, by.y));
	}
	
	public Vector2D add(double by){
		return new Vector2D(this.x + by, this.y + by);
	}
	public Vector2D subtract(double by){
		return new Vector2D(this.x - by, this.y - by);
	}
	public Vector2D multiply(double by){
		return new Vector2D(this.x * by, this.y * by);
	}
	public Vector2D divide(double by){
		return new Vector2D(doDivide(this.x, by), doDivide(this.y, by));
	}
	public Vector2D pow(double by){
		return new Vector2D(Math.pow(this.x, by), Math.pow(this.y, by));
	}
	
	public Vector2D round(){
		return new Vector2D(Math.round(this.x), Math.round(this.y));
	}
	/**
	 * 
	 * @param a First vector
	 * @param b Second vector
	 * @param c Third vector
	 * @return returns (a cross b) cross c
	 */
	public static Vector2D tripleProduct(Vector2D a, Vector2D b, Vector2D c){
		/*
		 * The internet told me to do this:
		 * return b.multiply(c.dotProduct(a)).subtract(a.multiply(c.dotProduct(b)));
		 * where I add a multiply function that accepts doubles.
		 * 
		 * but I figured out this other way that uses less arithmetic (9 arithmetic symbols vs 12)
		 */
		//A = (1, 0) B = (0, 1)
		//AB = (-1, 1)
		//AO = (-1, 0)
		return new Vector2D(-(c.y * ((a.x * b.y) - (b.x * a.y))), c.x * ((a.x * b.y) - (b.x * a.y)));
	}
	
	public static boolean counterClockwise(Vector2D a, Vector2D b, Vector2D c){
		return (b.x - a.x) * (c.y - a.y) > (b.y - a.y) * (c.x - a.x);
	}
	
	public Vector2D rightPerp(){
		return new Vector2D(-this.y, this.x);
	}
	public Vector2D leftPerp(){
		return new Vector2D(this.y, -this.x);
	}
	
	public byte side(Vector2D a, Vector2D b){
		double ret = ((b.x - a.x) * (this.y - a.y)) - ((b.y - a.y) * (this.x - a.x));
		if(ret == 0) return 0;
		return (byte) (ret / Math.abs(ret));
	}
	
	public double dot(Vector2D by) {
		return (this.x * by.x) + (this.y * by.y);
	}
	
	public Vector2D abs() {
		return new Vector2D(Math.abs(this.x), Math.abs(this.y));
	}
	
	public Vector2D negate() {
		return new Vector2D(-this.x, -this.y);
	}
	
	public Vector2D sign() {
		return this.divide(this.abs());
	}
	
	/**
	 * Returns a vector with a distance of 1 from the origin with the same x:y:z ratio as this vector
	 */
	public Vector2D normalize() {
		double distance = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
		return new Vector2D(doDivide(this.x, distance), doDivide(this.y, distance));
	}
	
	private static double doDivide(double a, double b){
		return (b == 0)? 0: a / b;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Vector2D){
			return (((Vector2D) o).x == this.x) && (((Vector2D) o).y == this.y);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "(" + this.x + ", " + this.y + ")";
	}
	
}
