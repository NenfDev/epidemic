##1.6.5 Update Notes
####Important changes to game mechanics, please read in full before updating

If you're updating from a pre 1.6.5 install of Epidemic, there are several major changes to the mechanics of 
Epidemic that should be considered BEFORE upgrading.  If you are a new user, or if you're doing a fresh
install, then please read the below, but don't worry about any of the conversion changes - there is a whole
set of pre-configured ailments and remedies you can use, modify or even discard and create your own.

Please note that when converting from a pre-1.6.5 version, the Ailments and your main config.yml files **will** be 
overwritten.  You must backup your Epidemic folder **BEFORE** updating as you will likely lose a number of
comments etc.

You will be required to restart the server one more time after the conversion in order to load the updated files
in correctly.

Version 1.6.5 contained a nearly complete rewrite of every major function inside the plugin.  This has led to a
number of changes in how the code works, the major ones of which are outlined below.  However, as with any major
update, certain things may no longer work quite as you expect.  If you have any questions or concerns with the
update process, please reach out to me on Discord.  https://discord.gg/7a47xSX

I'll be more than happy to explain the ailment/remedy changes in more detail, or even help out putting together
your ailments and remedies.

Major changes are listed below:

**Cures are no longer tied to Ailments**

Prior to 1.6.5, each ailment could have a single cure, which allowed the cure to be saved in the Ailment folder. This
is no longer the case.  With remedies, you can have multiple cures for a single ailment, or a single remedy that cures
multiple ailments.  During the conversion process, your cure will be exported out into a Remedy file.  It is highly
recommended that you review this prior to use.

**Secondary infections have been removed from the game**

Prior to version 1.5, only a single ailment could be applied to a player at a time.  This prevented any additional
ailment being used for the secondary infection.  With 1.5 multiple ailments became available as an optional config
setting.  With 1.6.5, this is no longer optional.  Multiple ailments are now enabled by default and cannot be changed.

As Epidemic can now handle multiple ailments easily, the secondary infections were a very limited approach to a
follow-up infection, instead they have been replaced with secondary ailments, which is a small but very important
distinction.  The major difference here is that the secondary ailment has its own ailment record.  It will still be
triggered in the same way as a secondary infection, but is truly its own ailment.  The other major difference this
causes is that previously you could not cure the original ailment until you had cured the infection, now you can cure
the original ailment or infection in either order.  If you cure the secondary ailment, it will not be applied again
by the original ailment (unless cured and then the player gets the ailment again).

With this change, we no longer needed the relief potion or infection cure from the main config.yml file - these have
been removed from the game.  The conversion process will automatically create the secondary infection ailments and
remedies for you, but again, it is **highly recommended** that you review these prior to use.

Please note that because the secondary ailment has its own ailment record, it could have its own secondary ailment of
its own.  This is referred to as ailment chaining and is NOT recommended or supported.

**Custom Drinks**

Custom drinks are a fairly new feature added to Epidemic with version 1.5.5, however, they mimic a number of 
features found in Remedies, so became redundant.  You can now create Remedies for each custom drink and modify
the various configuration options as needed.  The conversion process will automatically convert these for you.

**Temperature Relief Potions**

Similar to custom drinks, the temperature relief potions became redundant when alongside remedies as remedies
can apply heat/cold symptom relief.  The conversion process will automatically convert these for you.

**Default Thirst Potions**

Similar to custom drinks, the custom thirst potions became redundant when alongside remedies as remedies
can apply thirst modifiers, so have been replaced with remedies.  The conversion process will automatically convert 
these for you.

**Drinking from a non-boiled cauldron can now make you sick**

The same logic that decided if dirty water could make you sick has now been applied to drinking from a cauldron
without a campfire beneath it.

**/epidemic cures has been replaced with /epidemic.recipes**

As cures have been deprecated and are automatically converted, there is no need for /epidemic cures, however,
/epidemic recipes provides a very similar interface for remedies.

**Permission Changes**

The following permission changes have been made, however, the old permissions can be still be used, but are
considered deprecated and WILL be removed in an upcoming update.
epidemic.cures.take has been replaced with epidemic.remedy.take
epidemic.cures.display has been replaced with epidemic.recipes.display
