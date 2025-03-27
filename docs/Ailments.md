##Ailments

Ailments are any infectious/non-infectious illness or injury that can be applied to a player.  They can be set to 
be contagious or affect only the player who has it.  They can also lead to secondary ailments which afflict the player
after a set amount of time if the original ailment isn't cured in time.  You can even set the ailment to be hidden
from the player for a set amount of time, during which, they could be infecting any number of other players.

Each ailment has a number of possible configuration options to allow you to create extremely customized illnesses
and injuries that best fit your server.

####Ailment Configuration

Ailment files are .yml files stored in the ailments folder.  They should be named as the
internal_name (details below) .yml -  For example: broken_leg.yml

Ailment file version, this MUST match the currently required version and should not ever 
be modified by hand.  If the ailment configuration ever needs to update, it will replace
this value automatically.
> v: 1.1

Internal key name should be a unique value and should not include any spaces or most special characters
(underscores are acceptable.)  
> internal_name: "broken_leg"

Display name is how the ailment will be referred to in-game.  It can include color codes or any special character
that is allowed in-game.
> display_name: "Broken Leg"

The infectious flag indicates if this ailment can be passed along to other players.  If so, being close to someone
who has this ailment will increase your chance of catching the ailment.  This should only be set if the ailment
is infectious.  For example, you can't catch a broken leg.
> infectious: false

Infectious chance is the number between 0-10000 for a player getting this ailment via infectious means (not caused
by a fall or injury, but instead being around other sick players, or just being unlucky enough to get it).
Every 60 seconds (by default - this number can be modified in the config), each infectious ailment will generate
a random number between 0-10000, if that number is <= your infectious chance, you will get that ailment.  Your
infectious chance can be modified by other things, for example, a person near you with the ailment can boost your
chance of getting it, but having an immunity could lower your chance. 
> infectious_chance: 0

Non-Infectious chance is the number between 0-10000 for a player getting this ailment via non-infectious means, for
example, a fall or burn.  Non-infectious ailments need to be caused by something (covered later in the configuration),
and the chance of getting it works the same as infectious.  If the random number generated <= your non-infectious
chance, you will get the ailment.  Immunity and being around other contagious players etc. will not impact your 
non-infectious chance, however, if the damage chance modifier (covered later in the configuration) is set, your
chance could increase based on the damage you have received.
> non_infectious_chance: 250

Contagious chance boost is the amount to add on to your infectious chance if you are near a player who has the ailment.
For example, if you have an infectious chance of 200, and an infectious chance boost of 50, with the boost, if the 
random number was <= 200, you would get the ailment.  With the boost, if the number <= 250, you would get the ailment.
The boost is added to infectious_chance and used to calculate your chance of getting the ailment.
> contagious_chance_boost: 0

Maximum immunity amount is the maximum immunity you can gain to infectious ailments.  Your immunity will increase
based on the number of times you have recovered from an illness up the maximum set here.  The immunity is used
to lower your chance of getting an ailment.  For example, if you have a maximum immunity of 100, and your current
immunity is 80, and your infectious chance is 200 (with 0 boost), your new infectious chance would be 200 - 80 = 120
so you would only get the ailment if the random number generated was <= 120 instead of <= 200. 
> max_immunity: 0

Immunity modifier is the amount to add to your immunity for this ailment each time you recover from the ailment.
If your immunity modifier was 10 and your current immunity was 70, with a max_immunity of 80, the next time you 
were healed from this ailment, it would reach the maximum of 80.  If you got the ailment and was healed again, it
would remain 80.
> immunity_modifier: 0

Report before symptoms indicates if the ailment should show up when using the /health command if symptoms have not
yet been applied.  Setting this to false would show no ailment in /health if symptoms have not been applied, if they
have been applied, it would show the ailment.
> report_before_symptoms: true

If true, the warn on afflicted flag will send the afflicted_text to the player when the player gets the ailment.
This can be used in conjunction with report_before_symptoms = false to hide the ailment from the player until they
start to show symptoms.  This can be used to have the player spread the ailment to other people without their
knowledge.
> warn_on_afflicted: true

Afflicted text is the message to send to the player when they get the ailment (if warn_on_afflicted = true).  It
can contain color codes and any special characters that are allowed in-game.  If the message is left blank, no
message will be sent to the player. 
> afflicted_text: "Your leg breaks with a sickening crack"

Symptom text is the message to send to the player when symptoms first start.  This is especially useful if you have
a long period between affliction and symptoms.  It can contain color codes and any special character allowed in-game.
If this is left blank, no message will be sent.
> symptom_text: "The pain is starting to make you nauseous"

Infected Other text is the message the player receives if they cause another player to become sick through being
near them.  If you are hiding affliction from the player, you may want to leave this blank, however, if you don't
mind the player knowing that they have the affliction (purposefully spreading illness is valid), then you can
include the message for the player here.  It can contain color codes and any special character allowed in-game.
If this is left blank, no message will be sent.
> infected_other_text: ""

Healed text is the message sent to the player when they are cured of an ailment.  It can contain color codes and 
any special character allowed in-game. If this is left blank, no message will be sent.
> healed_text: "Your leg is feeling much better!"

Natural cure text is the message sent to the player if they are cured of an ailment over time.  It can contain
color codes and any special character allowed in-game.  If this is left blank, no message will be sent.
NOTE: Currently, there is no way to be cured naturally, but it is planned for 1.7.0
> natural_cure_text: "Your leg has improved and you can put weight on it again"

Time to symptoms is the number of seconds between a player getting an ailment, and them actually starting to show
symptoms for it.  For non-infectious ailments such as a broken leg, you might want symptoms to be immediate, in 
which case, set it to 0.  For infectious ailments, you may want the player to spread the ailment before they
start to show symptoms, in which case you can specify the number of seconds to wait.
> time_to_symptoms: 0

Is Fatal is a true/false flag for if the damage applied to the player can be fatal.  If false, the damage for the
player will stop if it would take to 0 health.  However, if they have a potion effect applied that also causes
damage, the player may still die even if the ailment itself is not fatal.  When false, the lowest health a player
can go to is half a heart.
> is_fatal: false

Damage is how much damage to apply to the player each time symptoms are applied.  By default, each 1 damage is
half of a hearts worth of damage, so if you wanted them to drop 2 hearts of damage, you'd set damage to 4.
Damage will not be applied if is_fatal = true and the damage would take them to 0 health.
> damage: 1

Secondary ailments are a pointer to another ailment that should be applied if the player does not cure the original
ailment within the amount of time specified.  Secondary ailments replace the 'secondary_infection' from previous
versions, and allows much more control over the secondary ailment.  For example, you could have Gangrene as a
secondary ailment for a broken leg, and Sepsis as a secondary ailment for a wound.  Each of those could have their
own remedies, and could apply vastly different effects.  Chaining secondary ailments is NOT recommended and NOT 
supported (chaining would be setting a secondary ailment on a secondary ailment).
Once a player has gotten a secondary ailment, they will not get that secondary ailment again if they cure it, until
the original ailment has been cured (or the player dies) and they get the original ailment a second time.  For
example: If a player gets a broken leg, then gets gangrene, they can cure the gangrene but not the broken leg, in
that case, the broken leg will not lead to gangrene a second time.  However, if they cure the broken leg and then
break their leg again, they can get gangrene as a secondary ailment.
> secondary_ailment:
>
> &nbsp;&nbsp;ailment: gangrene
>
> &nbsp;&nbsp;time: 360

If temperature is enabled and Fever is true, it will cause the player to overheat more quickly, raising their
core temperature.  This can cause the player to suffer temperature symptoms more easily.  Alternately, if the
player is cold, having a Fever will warm them up and may protect them from freezing.
> fever: false

If temperature is enabled and Chills is true, it will cause the player to freeze more quickly, lowering their
core temperature.  This can cause the player to suffer temperature symptoms more easily.  Alternately, if the
player is hot, having Chills will cool them down and may protect them from overheating.
> chills: false

If true, Display vomit will cause particle-effect vomit to be issued from the players mouth each time symptoms
are applied to the player. It is a visual effect only, but can be paired with making a player hungry by
lowering their hunger points.
> display_vomit: false

Display bleeding contains a number of true/false flags for if the player should appear to be bleeding from various
parts of the body.  You can use any combination of true/false to simulate bleeding from multiple body parts as 
needed.  This a visual effect only.  It can be paired with damage to simulate the player being cut
> display_bleeding:
>
> &nbsp;&nbsp;face: false
>
> &nbsp;&nbsp;head: false
>
> &nbsp;&nbsp;chest: false
>
> &nbsp;&nbsp;back: false
>
> &nbsp;&nbsp;left_arm: false
>
> &nbsp;&nbsp;right_arm: false
>
> &nbsp;&nbsp;left_leg: true
>
> &nbsp;&nbsp;right_leg: false

Display bowel, if true will simulate the player losing control of their bowels every time symptoms are applied to 
the player.  This is a visual effect only.
> display_bowel: false

Display urinate, if true will simulate the player losing control of their bladder every time symptoms are applied to 
the player.  This is a visual effect only.
> display_urinate: false

Display sweat, if true will simulate the player sweating every time symptoms are applied to the player.  This is a
visual effect only, but can be paired with fever in order to simulate a player overheating. 
> display_sweat: false

Display contagious, if true will display a small particle effect over the player when they are currently contagious.
This can be used to warn players to stay away from them.  This is a visual effect only.
> display_contagious: false

Display injury, if true will display a small particle effect over the player when they are injured (not contagious).
This could be used to indicate injury during pvp etc.  This is a visual effect only.
> display_injury: false

If insomnia is true, the player will be unable to sleep while they have this ailment.  This can cause issues for the
player such as sleeping to avoid Phantoms, or for recovering from ailments by sleeping (NOTE: planned for 1.7.0), or
even just for sleeping through the night.
> insomnia: false

If hallucinations are true, the player may hear random noises when symptoms are applied, such as the sound of a
creeper priming, a spider hissing or the sound of an arrow impacting etc.  This is an audio effect only currently
but visual hallucinations are plannned for the future.
> hallucination: false

If gibberish is true, any text the player types in chat will be scrambled.
> gibberish: false

If Food rot is true, then every time symptoms are applied to the player, any food they have on them will be 
changed into rotten flesh, mushroom or nothing (depending on the food)
> food_rot: false

Remove XP is a number and indicates the amount of XP to remove from the player each time symptoms are applied
to the player.  Keep in mind that symptoms (by default) are applied every 20 seconds, so setting the number to
10 for example, could potentially lead to losing 30XP per minute.
> remove_xp: 0

If Rust is true, damage will be applied to any iron items the player has on them, either wearing or in inventory. 
The amount of damage applied is indicated in rust_amount.
> rust: false

Rust amount is the amount of damage points to apply to each piece of iron armor/tool/weapon that the player has
on them, if rust is set to true.
> rust_amount: 0

If Clumsy is set to true, there is a 1/5 chance each time symptoms are applied that the player will drop
the item in their main hand.
> clumsy: false

Caused by fall is a true/false flag that will cause the ailment to be triggered if the damage type is from a fall.
This is for non-infectious chance only.
> caused_by_fall: true

Caused by injury is a true/false flag that will cause the ailment to be triggered if the damage type is from an injury,
such as being hit, shot etc. This is for non-infectious chance only.
> caused_by_injury: true

Caused by explosion is a true/false flag that will cause the ailment to be triggered if the damage type is explosion.
Explosion damage can be from a block such as TNT or entity such as a creeper. This is for non-infectious chance only.
> caused_by_explosion: true

Caused by fire is a true/false flag that will cause the ailment to be triggered if the player is burned. Burning can
come from fire, lava, magma blocks etc.  This is for non-infectious chance only.
> caused_by_fire: false

Caused by water is a true/false flag that will cause the ailment to be triggered by drowning damage.  This is for
non-infectious chance only.
> caused_by_water: false

Caused by consume is a true/false flag that will cause the ailment to be triggered by eating or drinking.  This is for
non-infecitous chance only.
> caused_by_consume: false

Cause by magic is a true/false flag that will cause the ailment to be triggered by damage caused by Magic.  This is
for non-infectious chance only.
> caused_by_magic: false

Caused by entity is a list of entities that can caused the ailment to be triggered if the player takes damage from
them.  For example, for Rabies, you may use - WOLF, then any time the player takes damage from a wolf, there is
a chance (non-infectious chance) that the player will get rabies.  You can add as many entity types as needed.
If you do not wish for this to be used, you can set it to [] or comment it out/remove it completely from the config.
> caused_by_entity: []

For ailment causes trigggered by player damage (fall, explosion, injury etc.) if the damage_chance_modifier > 0, then
the damage dealt will be multiple by the damage chance modifier and added to the chance of ailment.  Example, a player
falls, taking 1.5 hearts of damage (damage = 3), and triggering an ailment with a non-infectious chance of 50 and
a damage_chance_modifier of 3.  The new non-infectious chance would be 50 + (3 * 5) = 50 + 15 = 65 (chance is 
always rounded)
> damage_chance_modifier: 5;

Ailment effects is a group of settings that apply potion effects on to the player every time symptoms are applied
to the player.  You can add multiple potion effects, taking care of the spacing so time and amplifier are correctly
nested under each effect.  Ailment effects is the parent item, the potion effect is a child of that, and time and
amplifier is a child of the potion effect.  Time is the number of seconds to apply it for, and amplifier is how
strong the effect is.
> ailment_effects:
>
> &nbsp;&nbsp;SLOW:
>
> &nbsp;&nbsp;&nbsp;&nbsp;time: 60
>
> &nbsp;&nbsp;&nbsp;&nbsp;amplifier: 2
>
> &nbsp;&nbsp;WEAKNESS
>
> &nbsp;&nbsp;&nbsp;&nbsp;time: 5
>
> &nbsp;&nbsp;&nbsp;&nbsp;amplifier: 1

Particle on heal is the particle effect to apply to the player when the player is cured of the ailment.  If you do
not wish to have an effect, comment out or remove it from the config entirely.
> particle_on_heal: VILLAGER_HAPPY

Sound on heal is the sound to player at the players location when the player is cured of the ailment.  If you do not
wish to have a sound player, comment out or remove it from the config entirely.
> sound_on_heal: BREWING_STAND_BREW

Can Syringe is a true/false flag that indicates if a vial of blood can be extracted from the player when they have
this ailment.
NOTE: This is for 1.7.0 update and not currently in use
> can_syringe: false

Can Vaccinate is a true/false flag that indicates if the player can receive a vaccination that will increase their
immunity to this ailment.
NOTE: This is for 1.7.0 update and not currently in use
> can_vaccinate: false

Can gain immunity is a flag that indicates if the player can gain immunity to this ailment when they are cured of
the ailment.
> can_gain_immunity: false

Is Active is a true/false flag that indicates if the ailment should be added to the game.  If False, the ailment
will be ignored when loading the plugin.
> is_active: true