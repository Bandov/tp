package seedu.address.logic.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_MALFORMED_ATTRIBUTE_PAIR = "Attributes must be of the format: "
            + "/attributeName value";

    public static final String MESSAGE_INVALID_ROLE = "Roles must be all strings and one word only";

    public static final String MESSAGE_INVALID_ROLE_DELETE = "Roles does not need to be specified for delete command";

    /**
     * Parses a string into a string with 4 characters and whitespaces removed
     * @param uuid last 4 characters of a UUID
     * @return a trimmed String if uuid given as arguments is valid
     * @throws ParseException
     */
    public static String parseUuid(String uuid) throws ParseException {
        String trimmedUuid = uuid.trim();
        if (!StringUtil.isValidLastFourDigitsUuid(trimmedUuid)) {
            throw new ParseException(Messages.MESSAGE_INVALID_PERSON_UUID);
        }
        return trimmedUuid;
    }

    /**
     * Parses attributes in command arguments into a HashMap representing the pairs of attribute names and values
     *
     * @param parts The pairs of attribute names and values in the command arguments
     * @return  A HashMap containing the pairs of attribute names and values
     */
    public static HashMap<String, String> getAttributeHashMapFromAttributeStrings(String[] parts)
            throws ParseException {
        requireValidParts(parts);
        HashMap<String, String> attributeMap = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            String[] attribute = separateAttributeNamesAndValues(parts[i]);
            String attributeName = attribute[0];
            String attributeValue = attribute[1];
            attributeMap.put(attributeName, attributeValue);
        }
        return attributeMap;
    }
    /**
     * Removes the first item from a string list
     *
     * @param parts The string list
     * @return A new string list with the first item removed
     */
    public static String[] removeFirstItemFromStringList(String[] parts) {
        if (parts.length == 0) {
            throw new IllegalArgumentException("The parts array should not be empty.");
        }
        // Solution below generated by GitHub Copilot
        String[] newParts;
        newParts = new String[parts.length - 1];
        System.arraycopy(parts, 1, newParts, 0, parts.length - 1);
        return newParts;
    }

    private static String[] separateAttributeNamesAndValues(String parts) throws ParseException {
        String[] result = parts.trim().split(" ", 2);
        if (result.length != 2) {
            throw new ParseException(String.format(MESSAGE_MALFORMED_ATTRIBUTE_PAIR));
        }
        result[0] = result[0].trim();
        result[1] = result[1].trim();
        return result;
    }

    /**
     * Parses relationships in command arguments into a LinkedHashMap representing the pairs of UUIDs and values
     *
     * @param parts The pairs of UUIDs and values in the command arguments
     * @return  A LinkedHashMap containing the pairs of UUIDs and values
     */
    public static String[] separateUuidAndValues(String parts) throws ParseException {
        String[] result = parts.trim().split(" ");
        if (result.length == 1) {
            result[0] = result[0].trim();
            return result;
        } else if (result.length == 2) {
            if (!result[1].matches("[a-zA-Z]+")) {
                throw new ParseException(String.format(MESSAGE_INVALID_ROLE));
            }
            result[0] = result[0].trim();
            result[1] = result[1].trim();
            return result;
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_ROLE));
        }
    }

    private static String[] separateRelationshipTypes(String parts) {
        String[] result = parts.trim().split(" ", 1);
        result[0] = result[0].trim();
        return result;
    }

    private static void requireValidParts(String[] parts) throws ParseException {
        for (int i = 0; i < parts.length; i++) {
            separateAttributeNamesAndValues(parts[i]);
        }
    }

    /**
     * Parses relationships in command arguments into a LinkedHashMap representing the pairs of UUIDs and values
     *
     * @param parts The pairs of UUIDs and values in the command arguments
     * @return  A LinkedHashMap containing the pairs of UUIDs and values
     */
    public static LinkedHashMap<String, String> getRelationshipHashMapFromRelationshipStrings(String[] parts)
            throws ParseException {
        LinkedHashMap<String, String> relationshipMap = new LinkedHashMap<>();

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                String[] uuidAndValue = separateUuidAndValues(parts[i]);
                String uuid = uuidAndValue[0];
                String value;
                if (uuidAndValue[0].equals("")) {
                    throw new ParseException("UUIDs cannot be empty.");
                }
                if (uuidAndValue.length == 1) {
                    value = null;
                } else {
                    value = uuidAndValue[1];
                }
                relationshipMap.put(uuid, value);
            } else if (i == 1) {
                String[] uuidAndValue = separateUuidAndValues(parts[i]);
                String value = relationshipMap.keySet().toArray(new String[0])[0];
                if (uuidAndValue[0].equals("")) {
                    throw new ParseException("UUIDs cannot be empty.");
                }
                if (uuidAndValue[0].equals(value)) {
                    throw new ParseException("Relationships must be between 2 different people");
                }
                String uuid = uuidAndValue[0];
                String value2;
                if (uuidAndValue.length == 1) {
                    value2 = null;
                } else {
                    if (uuidAndValue[1].equals(relationshipMap.values().toArray(new String[0])[0])
                            && !separateRelationshipTypes(parts[2])[0].equalsIgnoreCase("siblings")
                            && !separateRelationshipTypes(parts[2])[0].equalsIgnoreCase("spouses")) {
                        throw new ParseException("Roles must be different for each person in a relationship.");
                    }
                    value2 = uuidAndValue[1];
                }
                relationshipMap.put(uuid, value2);
            } else if (i == 2) {
                String[] relationshipType = separateRelationshipTypes(parts[i]);
                String relationshipTypeKey = relationshipType[0];
                relationshipMap.put(relationshipTypeKey, null);
                if (relationshipType[0].equals("")) {
                    throw new ParseException("Relationship type cannot be empty.");
                }
            }
        }
        return relationshipMap;
    }

    /**
     * Parses relationships in command arguments into a LinkedHashMap representing the pairs of UUIDs and values
     *
     * @param parts The pairs of UUIDs and values in the command arguments
     * @return  A LinkedHashMap containing the pairs of UUIDs and values
     */
    public static LinkedHashMap<String, String> getRelationshipHashMapDelete(String[] parts, boolean hasUuids)
            throws ParseException, CommandException {
        LinkedHashMap<String, String> relationshipMap = new LinkedHashMap<>();

        if (hasUuids) {
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    String[] uuidAndValue = separateUuidAndValuesDelete(parts[i]);
                    relationshipMap.put(uuidAndValue[0], null);
                } else if (i == 1) {
                    String[] uuidAndValue = separateUuidAndValuesDelete(parts[i]);
                    String value = relationshipMap.keySet().toArray(new String[0])[0];
                    if (uuidAndValue[0].equals(value)) {
                        throw new CommandException("Relationships must be between 2 different people");
                    }
                    relationshipMap.put(uuidAndValue[0], null);
                } else if (i == 2) {
                    String[] relationshipType = separateRelationshipTypes(parts[i]);
                    String relationshipTypeKey = relationshipType[0];
                    relationshipMap.put(relationshipTypeKey, null);
                }
            }
        } else {
            String[] relationshipType = separateRelationshipTypes(parts[0]);
            String relationshipTypeKey = relationshipType[0];
            relationshipMap.put(relationshipTypeKey, null);
        }
        return relationshipMap;
    }

    /**
     * Parses relationships in command arguments into a LinkedHashMap representing the pairs of UUIDs and values
     *
     * @param parts The pairs of UUIDs and values in the command arguments
     * @return  A LinkedHashMap containing the pairs of UUIDs and values
     */
    public static String[] separateUuidAndValuesDelete(String parts) throws ParseException {
        String[] result = parts.trim().split(" ");
        if (result.length != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_ROLE_DELETE));
        } else {
            result[0] = result[0].trim();
            return result;
        }
    }

    /**
     * Parses relationships in command arguments into a LinkedHashMap representing the pairs of UUIDs and values
     *
     * @param parts The pairs of UUIDs and values in the command arguments
     * @return  A LinkedHashMap containing the pairs of UUIDs and values
     */
    public static LinkedHashMap<String, String> getRelationshipHashMapEdit(String[] parts)
            throws ParseException {
        LinkedHashMap<String, String> relationshipMap = new LinkedHashMap<>();

        for (int i = 0; i < parts.length; i++) {
            String value;
            if (i == 0) {
                String uuid = separateUuidAndValues(parts[i])[0];
                if (separateUuidAndValues(parts[i]).length == 1) {
                    value = null;
                } else {
                    value = separateUuidAndValues(parts[i])[1];
                }
                relationshipMap.put(uuid, value);
            } else if (i == 1) {
                if (separateUuidAndValues(parts[i])[0].equalsIgnoreCase(
                        relationshipMap.keySet().toArray(new String[0])[0])) {
                    throw new ParseException("Relationships must be between 2 different people");
                }
                String uuid = separateUuidAndValues(parts[i])[0];
                if (separateUuidAndValues(parts[i]).length == 1) {
                    value = null;
                } else {
                    if (separateUuidAndValues(parts[i])[1].equals(relationshipMap.values().toArray(new String[0])[0])
                            && !separateRelationshipTypes(parts[3])[0].equalsIgnoreCase("siblings")
                            && !separateRelationshipTypes(parts[3])[0].equalsIgnoreCase("spouses")) {
                        throw new ParseException("Roles must be different for each person in a relationship.");
                    }
                    value = separateUuidAndValues(parts[i])[1];
                }
                relationshipMap.put(uuid, value);
            } else if (i == 2) {
                String[] relationshipType = separateRelationshipTypes(parts[i]);
                String relationshipTypeKey = relationshipType[0];
                relationshipMap.put(relationshipTypeKey, null);
            } else if (i == 3) {
                String[] relationshipType = separateRelationshipTypes(parts[i]);
                String relationshipTypeKey = relationshipType[0];
                relationshipMap.put(relationshipTypeKey, null);
            }
        }
        return relationshipMap;
    }

    /**
     * Returns the key or value of a LinkedHashMap at a specified index
     *
     * @param relationshipMap The LinkedHashMap containing the pairs of UUIDs and values
     * @param index The index of the key or value to return
     * @param value A boolean indicating whether to return the key or value
     * @return The key or value at the specified index
     */
    public static String relationKeysAndValues(LinkedHashMap<String, String> relationshipMap,
                                               int index, boolean value) {
        if (index >= relationshipMap.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (!value) {
            return relationshipMap.keySet().toArray(new String[0])[index];
        } else {
            return relationshipMap.values().toArray(new String[0])[index];
        }
    }

    /**
     * Validates the roles for familial relationships
     *
     * @param relationshipDescriptor The type of familial relationship
     * @param relationshipMap The LinkedHashMap containing the pairs of UUIDs and values
     * @throws ParseException If the roles are invalid
     */
    public static void validateRolesForFamilialRelation(String relationshipDescriptor, LinkedHashMap<String,
            String> relationshipMap) throws ParseException {
        if (ParserUtil.relationKeysAndValues(relationshipMap, 0, true) == null
                || ParserUtil.relationKeysAndValues(relationshipMap, 1, true) == null) {
            throw new ParseException(relationshipDescriptor + " relationship requires two roles to be specified.\n"
                    + "Please specify the roles in the format: "
                    + "\naddRelation /<UUID> <role> /<UUID> <role> /" + relationshipDescriptor);
        }
        String role1 = ParserUtil.relationKeysAndValues(relationshipMap, 0, true).toLowerCase();
        String role2 = ParserUtil.relationKeysAndValues(relationshipMap, 1, true).toLowerCase();

        switch (relationshipDescriptor) {
        case "bioparents":
            if (!role1.equalsIgnoreCase("parent") && !role1.equalsIgnoreCase("child")
                    && !role2.equalsIgnoreCase("parent")
                    && !role2.equalsIgnoreCase("child")) {
                throw new ParseException("BioParents relationship requires the roles to be "
                        + "specified as either 'parent' or 'child'.");
            }
            break;
        case "siblings":
            if (!role1.equalsIgnoreCase("brother") && !role1.equalsIgnoreCase("sister")
                    && !role2.equalsIgnoreCase("brother")
                    && !role2.equalsIgnoreCase("sister")) {
                throw new ParseException("Siblings relationship requires the roles to be "
                        + "specified as either 'brother' or 'sister'.");
            }
            break;
        case "spouses":
            if (!role1.equalsIgnoreCase("husband") && !role1.equalsIgnoreCase("wife")
                    && !role2.equalsIgnoreCase("husband")
                    && !role2.equalsIgnoreCase("wife")) {
                throw new ParseException("Spouses relationship requires the roles to be "
                        + "specified as either 'husband' or 'wife'.");
            }
            break;
        default:
            throw new IllegalStateException("Unexpected value: " + relationshipDescriptor);
        }
    }

}
