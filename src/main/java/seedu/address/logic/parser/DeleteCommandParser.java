package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        if (!args.trim().startsWith("/")) {
            throw new ParseException("Please input a UUID prefixed with '/'." + "\n" + DeleteCommand.MESSAGE_USAGE);
        }
        return new DeleteCommand(args.trim().substring(1).trim());
    }
}
