package seedu.address.logic.relationship;

import static seedu.address.model.Model.PREDICATE_SHOW_NO_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_NO_RELATIONSHIPS;

import java.util.UUID;

import seedu.address.commons.util.ResultContainer;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.PersonInRelationshipPathwayPredicate;
import seedu.address.model.person.RelationshipInRelationshipPathwayPredicate;

/**
 * This class is responsible for parsing and executing commands to search for the relationship pathway between two
 * persons in the address book.
 */
public class AnySearchCommand extends Command {

    public static final String COMMAND_WORD = "anySearch";

    private String originUuid;
    private String targetUuid;

    /**
     * Constructor takes in the string arguments needed to be passed into the relationship constructor and performs
     * the search for the relationship pathway.
     * @param originUuid
     * @param targetUuid
     */
    public AnySearchCommand(String originUuid, String targetUuid) {
        this.originUuid = originUuid;
        this.targetUuid = targetUuid;
    }

    /**
     * Executes the command to search for the relationship pathway between two persons
     * @param model
     * @return CommandResult
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (originUuid == null || targetUuid == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_UUID);
        }
        UUID fullOriginUuid = model.getFullUuid(originUuid);
        UUID fullTargetUuid = model.getFullUuid(targetUuid);
        if (fullOriginUuid == fullTargetUuid) {
            throw new CommandException("anySearch must be performed between two different persons.");
        }
        ResultContainer searchResult = model.anySearch(fullOriginUuid, fullTargetUuid);
        if (searchResult == null) {
            model.updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS);
            model.updateFilteredRelationshipList(PREDICATE_SHOW_NO_RELATIONSHIPS);
            return new CommandResult(Messages.MESSAGE_SEARCH_FAILURE);
        }
        model.updateFilteredPersonList(new PersonInRelationshipPathwayPredicate(searchResult.getPersons()));
        model.updateFilteredRelationshipList(
                new RelationshipInRelationshipPathwayPredicate(searchResult.getRelationships()));
        return new CommandResult(String.format("Pathway:\n%s",searchResult.getRelationshipPathway()), 
                false, false, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AnySearchCommand // instanceof handles nulls
                && originUuid.equals(((AnySearchCommand) other).originUuid)
                && targetUuid.equals(((AnySearchCommand) other).targetUuid));
    }
}
