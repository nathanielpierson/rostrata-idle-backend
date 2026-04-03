package com.rostrata.idle.fish;

import com.rostrata.idle.storage.StorageDelta;
import com.rostrata.idle.storage.StorageService;
import com.rostrata.idle.user.User;
import com.rostrata.idle.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FishService {

    private final FishRepository fishRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    private static final Pattern TIME_TOKEN = Pattern.compile("(\\d+)([ms])");

    public FishService(FishRepository fishRepository, UserRepository userRepository, StorageService storageService) {
        this.fishRepository = fishRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    public List<Fish> getAllFish() {
        return fishRepository.findAll();
    }

    @Transactional
    public Fish createOrUpdate(FishCreateRequest request) {
        String name = requireNonBlank(request.name(), "name");
        String imageUrl = requireNonBlank(request.imageUrl(), "imageUrl");
        Integer levelRequirement = request.levelRequirement();
        if (levelRequirement == null) {
            throw new IllegalArgumentException("levelRequirement is required");
        }
        Integer xpGiven = request.xpGiven();
        if (xpGiven == null || xpGiven < 0) {
            throw new IllegalArgumentException("xpGiven must be >= 0");
        }

        int minSec = parseTimeToSeconds(request.minTimeToFish(), "minTimeToFish");
        int maxSec = parseTimeToSeconds(request.maxTimeToFish(), "maxTimeToFish");
        if (minSec > maxSec) {
            throw new IllegalArgumentException("minTimeToFish must be <= maxTimeToFish");
        }

        Optional<Fish> existing = fishRepository.findByName(name);
        if (existing.isEmpty()) {
            existing = findExistingByNormalizedName(name);
        }
        if (existing.isPresent()) {
            Fish fish = existing.get();
            fish.setName(name);
            fish.setMinSecondsToFish(minSec);
            fish.setMaxSecondsToFish(maxSec);
            fish.setImageUrl(imageUrl);
            fish.setLevelRequirement(levelRequirement);
            fish.setXpGiven(xpGiven);
            return fishRepository.save(fish);
        }

        return fishRepository.save(new Fish(name, levelRequirement, minSec, maxSec, xpGiven, imageUrl));
    }

    private Optional<Fish> findExistingByNormalizedName(String name) {
        String target = normalizeFishName(name);
        return fishRepository.findAll().stream()
                .filter(f -> normalizeFishName(f.getName()).equals(target))
                .findFirst();
    }

    private static String normalizeFishName(String name) {
        return name.toLowerCase().replaceAll("\\s+", "");
    }

    /**
     * Default fish: level, XP, image URLs; time range is min–max seconds (random per catch on client).
     */
    @Transactional
    public List<Fish> seedDefaultFish() {
        List<FishCreateRequest> defaults = List.of(
                f("Minnow", 1, 7, 11, 15,
                        "https://www.thesprucepets.com/thmb/7AK8RyEX0Wid380e5x8Qt46Ugqc=/2646x0/filters:no_upscale():strip_icc()/white-cloud-mountain-minnow-1380870-hero-6ba019390bc04253a537f30061617ea7.jpg"),
                f("Shrimp", 5, 3, 15, 22,
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJLpQO3qaEAzZ7xZJR0F5D4_Iy1u8dZxjgWw&s"),
                f("Butterfish", 10, 9, 13, 35,
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9qPs-xgxRYY4egtomwVxfRuNmiB7WOyIinw&s"),
                f("Crayfish", 12, 6, 19, 42,
                        "https://morethanadodo.com/wp-content/uploads/2017/08/cherax-snowdoni-1024px.jpg"),
                f("Tuna", 15, 11, 15, 55,
                        "https://natureconservancy-h.assetsadobe.com/is/image/content/dam/tnc/nature/en/photos/Minden_90220512_1640x1230.jpg?crop=0%2C164%2C1640%2C902&wid=1300&hei=715&scl=1.2615384615384615"),
                f("Tilapia", 20, 13, 17, 70,
                        "https://images.thefishsite.com/fish/articles/Feed/Nile-tilapia-credit-shutterstock.jpg?width=650&height=0"),
                f("Bass", 30, 15, 20, 120,
                        "https://flylifemagazine.com/wp-content/uploads/2014/05/bass1.jpg"),
                f("Weakfish", 40, 17, 23, 175,
                        "https://upload.wikimedia.org/wikipedia/commons/1/1a/Cynot_u3.jpg"),
                f("Catfish", 50, 19, 26, 240,
                        "https://thevlm.org/wp-content/uploads/white-catfish-face.jpg"),
                f("Tilefish", 60, 21, 28, 320,
                        "https://safmc.net/wp-content/uploads/2022/01/s-tilefish.jpeg"),
                f("Sturgeon", 70, 23, 30, 400,
                        "https://upload.wikimedia.org/wikipedia/commons/a/ab/Huge_sturgeon_in_the_Gulf_of_St._Lawrence_ecosystem_-_panoramio.jpg"),
                f("Scorpionfish", 75, 25, 33, 480,
                        "https://a-z-animals.com/media/scorpion-fish-3.jpg"),
                f("Flounder", 80, 30, 50, 560,
                        "https://scaquarium.org/wp-content/uploads/2015/11/sc-aquarium-flounder-animal-spec-sheet.jpg")
        );

        return defaults.stream().map(this::createOrUpdate).toList();
    }

    private static FishCreateRequest f(
            String name,
            int level,
            int minSec,
            int maxSec,
            int xpGiven,
            String imageUrl
    ) {
        return new FishCreateRequest(
                name,
                imageUrl,
                level,
                Integer.toString(minSec),
                Integer.toString(maxSec),
                xpGiven
        );
    }

    @Transactional
    public CatchResult catchFish(User user, Long fishId) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));
        Fish fish = fishRepository.findById(fishId)
                .orElseThrow(() -> new IllegalArgumentException("Fish not found: " + fishId));

        long xpToAdd = fish.getXpGiven();
        long before = managedUser.getFishingXp() == null ? 0L : managedUser.getFishingXp();
        long after = before + xpToAdd;
        managedUser.setFishingXp(after);
        userRepository.save(managedUser);

        String itemKey = "fishing_catch_" + fish.getId();
        String itemName = fish.getName();
        String itemImageUrl = fish.getImageUrl();

        StorageDelta delta = storageService.addToStorage(
                managedUser,
                itemKey,
                itemName,
                itemImageUrl,
                1L
        );

        return new CatchResult(managedUser.getId(), fish.getId(), xpToAdd, after, List.of(delta));
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    /**
     * Converts a string like "10s", "1m", or "1m20s", or plain seconds "7", into total seconds.
     */
    private static int parseTimeToSeconds(String time, String fieldName) {
        if (time == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        String normalized = time.trim().toLowerCase().replaceAll("\\s+", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (normalized.matches("\\d+")) {
            return Integer.parseInt(normalized);
        }

        int totalSeconds = 0;
        int lastEnd = 0;

        Matcher matcher = TIME_TOKEN.matcher(normalized);
        while (matcher.find()) {
            if (matcher.start() != lastEnd) {
                throw new IllegalArgumentException("Invalid " + fieldName + " format: " + time);
            }

            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            if (unit.equals("m")) {
                totalSeconds = Math.toIntExact(totalSeconds + value * 60L);
            } else if (unit.equals("s")) {
                totalSeconds = Math.toIntExact(totalSeconds + value);
            } else {
                throw new IllegalArgumentException("Invalid " + fieldName + " unit: " + time);
            }

            lastEnd = matcher.end();
        }

        if (lastEnd != normalized.length() || totalSeconds <= 0) {
            throw new IllegalArgumentException("Invalid " + fieldName + " format: " + time);
        }

        return totalSeconds;
    }

    public record CatchResult(
            Long userId,
            Long fishId,
            Long xpGranted,
            Long fishingXpTotal,
            List<StorageDelta> storageDeltas
    ) {
    }
}
