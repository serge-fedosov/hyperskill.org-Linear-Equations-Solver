
class Book {

    private String title;
    private int yearOfPublishing;
    private String[] authors;

    public Book(String title, int yearOfPublishing, String[] authors) {
        this.title = title;
        this.yearOfPublishing = yearOfPublishing;
        this.authors = authors;
    }

    private String authorsString(String[] authors) {
        String str = "";
        for (var author : authors) {
            str = str + author + ",";
        }
        return "[" + str.substring(0, str.length() - 1) + "]";
    }

    @Override
    public String toString() {
        return "title=" + title +
                ",yearOfPublishing=" + yearOfPublishing +
                ",authors=" + authorsString(authors);
    }
}