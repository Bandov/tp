package seedu.address.logic.relationship;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddRelationshipCommand object
 */
public class AddRelationshipCommandParser implements Parser<AddRelationshipCommand> {
    /**
     * Parses a userInput into the arguments to add a relationship to AB3
     *
     * @param userInput user-input command
     * @return an addRelationshipCommand with the necessary arguments
     */
    public AddRelationshipCommand parse(String userInput) throws ParseException {
        requireNonNull(userInput);
        String[] parts = userInput.split("/", -1);
        if (parts.length != 4) {
            throw new ParseException(Messages.MESSAGE_INVALID_RELATIONSHIP_COMMAND_FORMAT);
        }
        parts = ParserUtil.removeFirstItemFromStringList(parts);
        LinkedHashMap<String, String> relationshipMap = ParserUtil.getRelationshipHashMapFromRelationshipStrings(parts);

        if ((ParserUtil.relationKeysAndValues(relationshipMap, 0, true) == null
                && ParserUtil.relationKeysAndValues(relationshipMap, 1, true) != null)
                || (ParserUtil.relationKeysAndValues(relationshipMap, 0, true) != null
                && ParserUtil.relationKeysAndValues(relationshipMap, 1, true) == null)) {
            throw new ParseException(Messages.MESSAGE_INVALID_RELATIONSHIP_COMMAND_FORMAT);
        }

        String originUuid = ParserUtil.relationKeysAndValues(relationshipMap, 0, false);
        String targetUuid = ParserUtil.relationKeysAndValues(relationshipMap, 1, false);
        String relationshipDescriptor = ParserUtil.relationKeysAndValues(relationshipMap,
                2, false).toLowerCase();

        String role1;
        String role2;

        if (relationshipDescriptor.equalsIgnoreCase("bioparents")
                || relationshipDescriptor.equalsIgnoreCase("siblings")
                || relationshipDescriptor.equalsIgnoreCase("spouses")) {
            ParserUtil.validateRolesForFamilialRelation(relationshipDescriptor, relationshipMap);
            role1 = ParserUtil.relationKeysAndValues(relationshipMap, 0, true).toLowerCase();
            role2 = ParserUtil.relationKeysAndValues(relationshipMap, 1, true).toLowerCase();

            return new AddRelationshipCommand(originUuid, targetUuid, relationshipDescriptor, role1, role2);
        }
        if ((ParserUtil.relationKeysAndValues(relationshipMap, 0, true) != null
                && ParserUtil.relationKeysAndValues(relationshipMap, 1, true) != null)) {
            role1 = ParserUtil.relationKeysAndValues(relationshipMap, 0, true).toLowerCase();
            role2 = ParserUtil.relationKeysAndValues(relationshipMap, 1, true).toLowerCase();

            if (relationshipDescriptor.equals("family") || relationshipDescriptor.equals("familys")) {
                throw new ParseException("Please specify the type of familial relationship instead of 'Family'.\n"
                        + " Valid familial relations are: [bioParents, siblings, spouses]");
            }
            return new AddRelationshipCommand(originUuid, targetUuid, relationshipDescriptor, role1, role2);
        } else {
            if (relationshipDescriptor.equals("family") || relationshipDescriptor.equals("familys")) {
                throw new ParseException("Please specify the type of familial relationship instead of 'Family'.\n"
                        + " Valid familial relations are: [bioParents, siblings, spouses]");
            }
            return new AddRelationshipCommand(originUuid, targetUuid, relationshipDescriptor);
        }
    }
}
