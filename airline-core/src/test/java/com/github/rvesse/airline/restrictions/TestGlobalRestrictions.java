package com.github.rvesse.airline.restrictions;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.args.Args1;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;
import com.github.rvesse.airline.parser.errors.ParseCommandMissingException;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingValueException;
import com.github.rvesse.airline.restrictions.global.CommandRequiredRestriction;
import com.github.rvesse.airline.restrictions.global.NoMissingOptionValuesRestriction;
import com.github.rvesse.airline.restrictions.global.NoUnexpectedArgumentsRestriction;

public class TestGlobalRestrictions {

    @Command(name = "empty", description = "Empty Command")
    public static class EmptyCommand {

    }

    @Cli(name = "cli", description = "Test CLI", commands = { Args1.class,
            EmptyCommand.class }, includeDefaultRestrictions = false)
    private class UnrestrictedCli {

    }

    @Test
    public void global_restrictions_unrestricted_no_command() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(UnrestrictedCli.class);
        Object cmd = parser.parse(new String[0]);
        Assert.assertNull(cmd);
    }

    @Test
    public void global_restrictions_unrestricted_missing_option_value() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(UnrestrictedCli.class);
        Object cmd = parser.parse(new String[] { "Args1", "-long" });
        Assert.assertTrue(cmd instanceof Args1);
    }
    
    @Test
    public void global_restrictions_unrestricted_unexpected_arguments() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(UnrestrictedCli.class);
        Object cmd = parser.parse(new String[] { "empty", "test" });
        Assert.assertTrue(cmd instanceof EmptyCommand);
    }

    @Cli(name = "cli", description = "Test CLI", commands = { Args1.class,
            EmptyCommand.class }, includeDefaultRestrictions = false, restrictions = {
                    CommandRequiredRestriction.class })
    private class CommandRequiredCli {

    }

    @Test(expectedExceptions = ParseCommandMissingException.class)
    public void global_restrictions_command_required_01() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(CommandRequiredCli.class);
        parser.parse(new String[0]);
    }

    @Test(expectedExceptions = ParseCommandUnrecognizedException.class)
    public void global_restrictions_command_required_02() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(CommandRequiredCli.class);
        parser.parse(new String[] { "foo" });
    }

    @Cli(name = "cli", description = "Test CLI", commands = { Args1.class,
            EmptyCommand.class }, includeDefaultRestrictions = false, restrictions = {
                    NoUnexpectedArgumentsRestriction.class })
    private class NoUnexpectedArgumentsCli {

    }

    @Test
    public void global_restrictions_no_unexpected_args_01() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(
                NoUnexpectedArgumentsCli.class);
        Object cmd = parser.parse(new String[] { "empty" });
        Assert.assertTrue(cmd instanceof EmptyCommand);
    }

    @Test
    public void global_restrictions_no_unexpected_args_02() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(
                NoUnexpectedArgumentsCli.class);
        Object cmd = parser.parse(new String[] { "Args1", "test" });
        Assert.assertTrue(cmd instanceof Args1);
        Args1 args = (Args1) cmd;
        Assert.assertEquals(args.parameters.size(), 1);
        Assert.assertEquals(args.parameters.get(0), "test");
    }

    @Test(expectedExceptions = ParseArgumentsUnexpectedException.class)
    public void global_restrictions_no_unexpected_args_03() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(
                NoUnexpectedArgumentsCli.class);
        parser.parse(new String[] { "empty", "test" });
    }

    @Cli(name = "cli", description = "Test CLI", commands = { Args1.class,
            EmptyCommand.class }, includeDefaultRestrictions = false, restrictions = {
                    NoMissingOptionValuesRestriction.class })
    private class NoMissingOptionValuesCli {

    }

    @Test(expectedExceptions = ParseOptionMissingValueException.class)
    public void global_restrictions_missing_option_values_01() {
        com.github.rvesse.airline.Cli<Object> parser = new com.github.rvesse.airline.Cli<>(
                NoMissingOptionValuesCli.class);
        parser.parse(new String[] { "Args1", "-long" });
    }
}