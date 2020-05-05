package utils;
import static java.lang.Math.*;

public class Complex {

    private double x;
    private double y;

    public Complex() {
	x = 0;
        y = 0;
    }

    public Complex(double real_part) {
        x = real_part;
        y = 0;
    }

    public Complex(double real_part, double imag_part) {
	x = real_part;
        y = imag_part;
    }

    public String toString() {
        String str = new String();
        if ( y == 0 ) {
	    str = Double.toString(x);
	}
	else if ( y > 0 ) {
	    str = Double.toString(x) + " + " + Double.toString(y) + " i";
	}
	else {
	    str = Double.toString(x) + " - " + Double.toString(-y) + " i";
	}
	return str;
    }

    public static String toString(Complex z) {
	return z.toString();
    }

    public String toStringPolar() {
	double r = this.abs();
        double theta = this.ang();
        String str = new String();
	if ( theta >= 0 ) {
	    str = Double.toString(r) + " exp(i " + Double.toString(theta) + ")";
	}
	else {
	    str = Double.toString(r) + " exp(-i " + Double.toString(-theta) + ")";
	}
	return str;
    }

    public double getRe() {
        return x;
    }

    public double getIm() {
        return y;
    }

    public void setRe(double a) {
        x = a;
    }

    public void setIm(double b) {
        y = b;
    }

    public Boolean isReal() {
	if ( y == 0 ){
	    return true;
	}
	else {
	    return false;
	}
    }

    public Boolean isImag() {
	if ( x == 0 ){
	    return true;
	}
	else {
	    return false;
	}
    }

    public double abs() {
        return Math.sqrt( x*x + y*y );
    }

    public double absSqr() {
        return x*x + y*y;
    }

    public double ang() {
        return Math.atan2(y,x);
    }

    public double angRad() {
        return this.ang();
    }

    public double angDeg() {
        return this.ang() * 180/Math.PI;
    }

    public Complex conj() {
        return new Complex( x, -y );
    }

    public Complex add(Complex z) {
	return new Complex( x + z.getRe(), y + z.getIm() );
    }

    public Complex add(double a) {
        return new Complex( x + a, y );
    }

    public Complex sub(Complex z) {
        return new Complex( x - z.getRe(), y - z.getIm() );
    }

    public Complex sub(double a) {
        return new Complex( x - a, y );
    }

    public Complex mult(Complex z) {
        double a = z.getRe();
        double b = z.getIm();
        return new Complex( x*a - y*b, x*b + y*a );
    }

    public Complex mult(double a) {
        return new Complex( a*x, a*y );
    }

    public Complex div(Complex z) {
        double a = z.getRe();
        double b = z.getIm();
        return new Complex( (x*a + y*b)/(a*a + b*b), (-x*b + y*a)/(a*a + b*b) );    }

    public Complex div(double a) {
	return new Complex( x/a, y/a );
    }

}
