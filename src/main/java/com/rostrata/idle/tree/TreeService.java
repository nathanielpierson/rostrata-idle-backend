package com.rostrata.idle.tree;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TreeService {

    private final TreeRepository treeRepository;

    private static final Pattern TIME_TOKEN = Pattern.compile("(\\d+)([ms])");

    public TreeService(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
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

        int secondsToChop = parseTimeToSeconds(request.timeToChop());

        Optional<Tree> existing = treeRepository.findByName(name);
        if (existing.isPresent()) {
            Tree tree = existing.get();
            tree.setSecondsToChop(secondsToChop);
            tree.setImageUrl(imageUrl);
            tree.setLevelRequirement(levelRequirement);
            return treeRepository.save(tree);
        }

        return treeRepository.save(new Tree(name, secondsToChop, imageUrl, levelRequirement));
    }

    @Transactional
    public List<Tree> seedDefaultTrees() {
        // Values provided by you:
        // name | image-url | level-required | time to chop
        List<TreeCreateRequest> defaults = List.of(
                new TreeCreateRequest(
                        "Bur Oak",
                        "https://scontent.fric1-2.fna.fbcdn.net/v/t51.75761-15/500482681_18323013763204797_5185688588855585874_n.jpg?stp=dst-jpegr_s1080x2048_tt6&_nc_cat=106&ccb=1-7&_nc_sid=13d280&_nc_ohc=ECYjqqC5NO0Q7kNvwEZ9Wdm&_nc_oc=Adn4-Nef1KXxlIdIoD6ZwWHVDHK_jH9O9OC7gtJ5fJc0Q6FUKBPcfBMJIo9QdckrBKQ&_nc_zt=23&se=-1&_nc_ht=scontent.fric1-2.fna&_nc_gid=ID0GTwlnUcOMZMIo2FvgmQ&_nc_ss=8&oh=00_Afzllo0n5zBwnmHEPhtZYeNlL_dABO3HvOXDKartkMNlcw&oe=69B39BD4",
                        1,
                        "10s"
                ),
                new TreeCreateRequest(
                        "Sawtooth Oak",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTeLO0UQ6SxrSZfxiPxOoy7Wh9MQzlh4nYJQA&s",
                        5,
                        "7s"
                ),
                new TreeCreateRequest(
                        "October Glory Maple",
                        "https://live.staticflickr.com/4517/38712137901_0b37ac4858_b.jpg",
                        15,
                        "15s"
                ),
                new TreeCreateRequest(
                        "Basswood",
                        "Basswood_forest_(14871993438).jpg",
                        40,
                        "20s"
                ),
                new TreeCreateRequest(
                        "Rostratian Sweetgum",
                        "https://upload.wikimedia.org/wikipedia/commons/6/6a/2014-11-02_13_06_29_Sweet_Gum_during_autumn_along_Lower_Ferry_Road_in_Ewing%2C_New_Jersey.JPG",
                        50,
                        "25s"
                ),
                new TreeCreateRequest(
                        "Loblolly Pine",
                        "https://live.staticflickr.com/65535/5454648928_2ffa54e74e_b.jpg",
                        60,
                        "30s"
                ),
                new TreeCreateRequest(
                        "Douglasfir",
                        "https://shop.arborday.org/treeguide/286",
                        70,
                        "35s"
                ),
                new TreeCreateRequest(
                        "Bald Cypress",
                        "https://live.staticflickr.com/747/20743193769_3d9784e77c_b.jpg",
                        80,
                        "45s"
                ),
                new TreeCreateRequest(
                        "Sycamore",
                        "https://upload.wikimedia.org/wikipedia/commons/f/fd/American-Sycamore-Bark.jpg",
                        90,
                        "1m"
                ),
                new TreeCreateRequest(
                        "Redwood",
                        "https://www.sustainability-times.com/wp-content/uploads/2024/01/redwood.webp",
                        95,
                        "1m20s"
                )
        );

        return defaults.stream().map(this::createOrUpdate).toList();
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
}

