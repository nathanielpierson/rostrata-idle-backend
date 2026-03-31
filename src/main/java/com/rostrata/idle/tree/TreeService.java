package com.rostrata.idle.tree;

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
public class TreeService {

    private final TreeRepository treeRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    private static final Pattern TIME_TOKEN = Pattern.compile("(\\d+)([ms])");

    public TreeService(TreeRepository treeRepository, UserRepository userRepository, StorageService storageService) {
        this.treeRepository = treeRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    public List<Tree> getAllTrees() {
        return treeRepository.findAll();
    }

    @Transactional
    public Tree createOrUpdate(TreeCreateRequest request) {
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

        int secondsToChop = parseTimeToSeconds(request.timeToChop());

        Optional<Tree> existing = treeRepository.findByName(name);
        if (existing.isEmpty()) {
            // Match legacy rows whose display name drifted (e.g. "Douglas Fir" vs "Douglasfir")
            existing = findExistingByNormalizedName(name);
        }
        if (existing.isPresent()) {
            Tree tree = existing.get();
            tree.setName(name);
            tree.setSecondsToChop(secondsToChop);
            tree.setImageUrl(imageUrl);
            tree.setLevelRequirement(levelRequirement);
            tree.setXpGiven(xpGiven);
            return treeRepository.save(tree);
        }

        return treeRepository.save(new Tree(name, secondsToChop, imageUrl, levelRequirement, xpGiven));
    }

    /**
     * Finds a tree whose name matches after lowercasing and removing all whitespace, so re-seeding
     * updates the same DB row even if spelling/spacing changed between app versions.
     */
    private Optional<Tree> findExistingByNormalizedName(String name) {
        String target = normalizeTreeName(name);
        return treeRepository.findAll().stream()
                .filter(t -> normalizeTreeName(t.getName()).equals(target))
                .findFirst();
    }

    private static String normalizeTreeName(String name) {
        return name.toLowerCase().replaceAll("\\s+", "");
    }

    @Transactional
    public List<Tree> seedDefaultTrees() {
        // Values provided by you:
        // name | image-url | level-required | time to chop
        List<TreeCreateRequest> defaults = List.of(
                new TreeCreateRequest(
                        "Bur Oak",
                        "https://www.adventuresci.org/wp-content/uploads/2021/10/Buroak1.png",
                        1,
                        "10s",
                        30
                ),
                new TreeCreateRequest(
                        "Sawtooth Oak",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTeLO0UQ6SxrSZfxiPxOoy7Wh9MQzlh4nYJQA&s",
                        5,
                        "7s",
                        35
                ),
                new TreeCreateRequest(
                        "October Glory Maple",
                        "https://live.staticflickr.com/4517/38712137901_0b37ac4858_b.jpg",
                        15,
                        "15s",
                        120
                ),
                new TreeCreateRequest(
                        "Basswood",
                        "https://upload.wikimedia.org/wikipedia/commons/2/21/Basswood_forest_%2814871993438%29.jpg",
                        40,
                        "20s",
                        175
                ),
                new TreeCreateRequest(
                        "Rostratian Sweetgum",
                        "https://upload.wikimedia.org/wikipedia/commons/6/6a/2014-11-02_13_06_29_Sweet_Gum_during_autumn_along_Lower_Ferry_Road_in_Ewing%2C_New_Jersey.JPG",
                        50,
                        "25s",
                        240
                ),
                new TreeCreateRequest(
                        "Loblolly Pine",
                        "https://live.staticflickr.com/65535/5454648928_2ffa54e74e_b.jpg",
                        60,
                        "30s",
                        333
                ),
                new TreeCreateRequest(
                        "Douglasfir",
                        "https://shop-static.arborday.org/media/0000454_douglasfir_510.jpg",
                        70,
                        "35s",
                        400
                ),
                new TreeCreateRequest(
                        "Bald Cypress",
                        "https://live.staticflickr.com/747/20743193769_3d9784e77c_b.jpg",
                        80,
                        "45s",
                        600
                ),
                new TreeCreateRequest(
                        "Sycamore",
                        "https://upload.wikimedia.org/wikipedia/commons/f/fd/American-Sycamore-Bark.jpg",
                        90,
                        "1m",
                        840
                ),
                new TreeCreateRequest(
                        "Redwood",
                        "https://www.sustainability-times.com/wp-content/uploads/2024/01/redwood.webp",
                        95,
                        "1m20s",
                        1250
                )
        );

        return defaults.stream().map(this::createOrUpdate).toList();
    }

    @Transactional
    public ChopResult chopTree(User user, Long treeId) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));
        Tree tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new IllegalArgumentException("Tree not found: " + treeId));

        long xpToAdd = tree.getXpGiven();
        long before = managedUser.getWoodcuttingXp() == null ? 0L : managedUser.getWoodcuttingXp();
        long after = before + xpToAdd;
        managedUser.setWoodcuttingXp(after);
        userRepository.save(managedUser);

        // Each chop grants 1 log into the player's storage.
        // Item key is based on the tree id so it stays stable even if display name changes.
        String itemKey = "woodcutting_log_" + tree.getId();
        String itemName = tree.getName() + " Logs";
        String itemImageUrl = tree.getImageUrl();

        StorageDelta delta = storageService.addToStorage(
                managedUser,
                itemKey,
                itemName,
                itemImageUrl,
                1L
        );

        return new ChopResult(managedUser.getId(), tree.getId(), xpToAdd, after, List.of(delta));
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    /**
     * Converts a string like "10s", "1m", or "1m20s" into total seconds.
     */
    private static int parseTimeToSeconds(String timeToChop) {
        if (timeToChop == null) {
            throw new IllegalArgumentException("timeToChop is required");
        }

        String normalized = timeToChop.trim().toLowerCase().replaceAll("\\s+", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("timeToChop is required");
        }

        if (normalized.matches("\\d+")) {
            return Integer.parseInt(normalized);
        }

        int totalSeconds = 0;
        int lastEnd = 0;

        Matcher matcher = TIME_TOKEN.matcher(normalized);
        while (matcher.find()) {
            if (matcher.start() != lastEnd) {
                throw new IllegalArgumentException("Invalid timeToChop format: " + timeToChop);
            }

            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            if (unit.equals("m")) {
                totalSeconds = Math.toIntExact(totalSeconds + value * 60L);
            } else if (unit.equals("s")) {
                totalSeconds = Math.toIntExact(totalSeconds + value);
            } else {
                throw new IllegalArgumentException("Invalid timeToChop unit: " + timeToChop);
            }

            lastEnd = matcher.end();
        }

        if (lastEnd != normalized.length() || totalSeconds <= 0) {
            throw new IllegalArgumentException("Invalid timeToChop format: " + timeToChop);
        }

        return totalSeconds;
    }

    public record ChopResult(
            Long userId,
            Long treeId,
            Long xpGranted,
            Long woodcuttingXpTotal,
            List<StorageDelta> storageDeltas
    ) {
    }
}

