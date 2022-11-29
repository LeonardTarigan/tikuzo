public class Utils {

    public String generateRandom() {
        String id = "";

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < 3; i++) {
            int rand = (int)(Math.random() * 10);
            id += String.valueOf(chars.charAt(rand));
        }

        int rand = (int)(Math.random() * 10000000);

        id += String.valueOf(rand);

        return id;
    }

    public String formatMoney(int num) {
        return String.format("Rp %,d", num).replace(",", ".");
    }
}
