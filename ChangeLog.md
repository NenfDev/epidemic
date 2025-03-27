Change Log 1.7.9
Bug Fixes
- Added Hazmat/Gas mask check for hotspots
- Added Gas mask check for block break
- Added Gas mask check for infectious


Change Log 1.7.0 - BIOLOGICAL WARFARE UPDATE

- Entity based vaccine development
- Illness based arrows/splash potions

Permission overhaul
epidemic.invincible removed, use epidemic.bypass instead

pre 1.6.5 backwards compatability removed


=========================================================================================


1.6.9
Added to ailment config to allow a biome modifier

  infectious_chance_biome_modifier:
    SWAMP: 100

Unlocked /infect to allow user to infect themselves

Added doctor role.  Right click a player 3 times to diagnose
Permission: epidemic.doctor
lang.yml:
    health_not_doctor: "&4Sorry, you must be a doctor"
    health_start_diagnosis: "&aDiagnosis started"
    remedy_doctor_only: "&cSorry, that remedy can only be applied by a doctor"
    remedy_others_fail: "&cSorry, that remedy cannot be applied to other players"

config.yml:
    diagnose_clicks: 3


----
- Doctor role (diagnose and heal)
- Block breaking causing illness (for example, redstone ore blocks have a chance of making you sick)
- Biome specific modifiers (more or less likely to get sick by biome) (also biome specific modifiers for block breaking)
- Infect command to allow self-infection
- Improvements to healing over time
- Healing by sleeping
- First aid container - basically a container that can only hold remedies
- MySQL support
----

=========================================================================================



Change Log 1.6.7
Bug fix - ENTITY_WITCH_CELEBRATE not available in 1.13.  Updated Hallucination code to play ENTITY_WITCH_AMBIENT in 1.13.
Debug messages no longer being sent when debug is off (two different debug classes caused that issue)
Nether biomes now considered in temperature check
All deprecated code removed (with exception of Misc.setXP)
pre-1.6.5 no longer supported.









Change Log 1.6.6 - CURRENT DEV BUILD


CHANGED chance to 1-10000 from 1-1000



- Rewrite config loader to use new config model
- Remove cure from Ailment class
- Rework cure consumption to use new custom cures
- Modify Cure > LegacyCure and CustomCure > Cure
- Load all ailments in the ailments folder, list of ailments removed from config.yml
- Secondary infections are being split out of the main ailment.  They will have their own ailment yml file as well as their own cure.  This will allow multiple different types of secondary infections, and infections will no longer prevent the root cause being cured.
- Cures will be split away from the ailment file and into their own cure yml file.  Each cure can be used to heal one or more ailment, as well as providing symptom relief, restoring hunger/thirst/health and providing potion effect boosts.   So with this fix, you could have a bandage that cures a wound and a broken leg, or a potion that provides symptom relief.
- Symptom relief potion and infection cures will be removed from the main config file and will be their own cure file.
- Mass updates to fix colored text issues throughout the plugin
- API events to be added each time a player becomes afflicted or when symptoms are applied
- allow_multiple_ailments removed - secondary infections would cause this, so no case exists to allow single ailment
- custom_drinks removed from config.yml - they can be added as cures with no ailment (see apple_juice.yml for example)
- temp_relief items removed from config.yml - they can be added as cures (see hot_cocoa.yml file for example)
- When clearing an affliction, check for other afflictions before removing potion effects
- Remove relief loading at the ailment level - should be at the player level
- Save/load relief to/from player yml

Change Log 1.6.2

- /epigive command added
- Previously deprecated sqlite file support removed
- Fix for damage to go beyond fatal
- Fix for null item meta error in some rare cases with custom items
- Water bottles can be disabled with: disable_water_bottle: false
- Water bowl can be disabled with: disable_water_bowl: false
- Respawn thirst level added - triggers if the player respawns with < 1 thirst left (indicating they likely died of thirst) with: respawn_thirst_level: <amount 0-100>
- Thirst now skips players not in SURVIVAL mode
- epidemic.admin no longer includes epidemic.invincible permission
- true/false in infected check of /health command now translatable
- Relief potion no longer works at the ailment level and is not persisted after restart (this will change in 1.6.3)

Change Log 1.6.1

This update addresses an issue that would prevent Epidemic running properly on new installs on pre-1.15 servers.
- Custom recipes in the ailments will no longer trigger an error in 1.13/1.14
- Sticky Egg in the default config will no longer cause issues in 1.13/1.14 and the base material is changed to LIME_DYE
- Empty ailment effects in the ailment config files will no longer trigger an Unexpected Error warning in the console

Change Log 1.6.0

This update brings Spigot/Paper 1.16.1 support to Epidemic

- Temperature that previously applied to the Nether is now applied across all Nether biomes
- New command:  /epidemic debug
    - You will need epidemic.admin permission to use this (or via console) - it will turn on/off the debug mode previously made available via the config file.
    
    