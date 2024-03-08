import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Contestant {
    String name;
    int points;

    Contestant(String name) {
        this.name = name;
        this.points = 0;
    }

    void addPoints(int points) {
        this.points += points;
    }

    @Override
    public String toString() {
        return name + ": " + points + " points";
    }
}

class Region {
    String name;
    List<Contestant> contestants;
    Map<String, Contestant> contestantMap;

    Region(String name) {
        this.name = name;
        this.contestants = new ArrayList<>();
        this.contestantMap = new HashMap<>();
    }

    void addContestant(String name) {
        Contestant contestant = new Contestant(name);
        contestants.add(contestant);
        contestantMap.put(name, contestant);
    }

    void vote(String[] preferences) {
        for (String preference : preferences) {
            Contestant contestant = contestantMap.get(preference);
            if (contestant != null) {
                contestant.addPoints(getPoints(preferences.length));
            }
        }
    }

    int getPoints(int preferenceCount) {
        if (preferenceCount == 1) return 3;
        else if (preferenceCount == 2) return 2;
        else return 1;
    }

    Contestant getWinner() {
        Contestant winner = contestants.get(0);
        for (Contestant contestant : contestants) {
            if (contestant.points > winner.points) {
                winner = contestant;
            }
        }
        return winner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Region: ").append(name).append("\n");
        for (Contestant contestant : contestants) {
            sb.append(contestant).append("\n");
        }
        return sb.toString();
    }
}

public class CodeElection {
    public static void main(String[] args) {
        Map<String, Region> regions = new HashMap<>();
        try {
            File file = new File("src/voting.dat");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("//") || line.equals("&&")) continue;

                String[] parts = line.split("/");
                if (parts.length != 2) {
                    System.out.println("Invalid input format: " + line);
                    continue;
                }
                String regionName = parts[0];
                String contestants = parts[1];

                Region region = new Region(regionName);
                for (char c : contestants.toCharArray()) {
                    region.addContestant(String.valueOf(c));
                }
                regions.put(regionName, region);

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine().trim();
                    if (line.equals("//")) break;

                    String[] voteParts = line.split(" ");
                    if (voteParts.length < 2) {
                        System.out.println("Invalid vote format: " + line);
                        continue;
                    }

                    String voterRegion = voteParts[0];
                    if (!voterRegion.equals(regionName)) continue;

                    String[] preferences = Arrays.copyOfRange(voteParts, 1, voteParts.length);
                    if (preferences.length > 3 || preferences.length == 0) continue;

                    region.vote(preferences);
                }
            }
            scanner.close();

            // Calculate winners
            Contestant chiefOfficer = null;
            for (Region region : regions.values()) {
                Contestant regionalHead = region.getWinner();
                System.out.println(region);
                System.out.println("Regional Head: " + regionalHead);
                if (chiefOfficer == null || regionalHead.points > chiefOfficer.points) {
                    chiefOfficer = regionalHead;
                }
            }
            System.out.println("Chief Officer: " + chiefOfficer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
