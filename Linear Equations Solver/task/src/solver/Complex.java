package solver;

public class Complex {
    double real;
    double image;

    Complex(double real, double image) {
        this.real = real;
        this.image = image;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImage() {
        return image;
    }

    public void setImage(double image) {
        this.image = image;
    }

    Complex add(Complex c) {
        return new Complex(real + c.getReal(), image + c.getImage());
    }

    Complex sub(Complex c) {
        return new Complex(real - c.getReal(), image - c.getImage());
    }

    Complex mul(Complex c) {
        return new Complex(real * c.getReal() - image * c.getImage(), image * c.getReal() + real * c.getImage());
    }

    Complex div(Complex c) {
        double newReal = (real * c.getReal() + image * c.getImage())/(c.getReal() * c.getReal() + c.getImage() * c.getImage());
        double newImage = (image * c.getReal() - real * c.getImage()) / (c.getReal() * c.getReal() + c.getImage() * c.getImage());
        return new Complex(newReal,newImage);
    }

    static boolean isZero(Complex c) {
        return c.getReal() == 0 && c.getImage() == 0;
    }

    static boolean isOne(Complex c) {
        return c.getReal() == 1 && c.getImage() == 0;
    }

    @Override
    public String toString() {
        if (real != 0 && image != 0) {
            if (image > 0) {
                return real + "+" + image + "i";
            } else {
                return real + "" + image + "i";
            }
        } else if (real == 0 && image == 0) {
            return "0";
        } else if (real == 0) {
            return image + "i";
        } else { // image == 0
            return "" + real;
        }
    }
}
