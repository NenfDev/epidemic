##Thirst

When enabled in the config file, temperature will be applied to each player in-game.  Being around too much of a
temperature extreme for too long will cause the player to have negative effects.

Temperature is calculated into a simple Level: COLD, NORMAL, HOT.

Players can check their current temperature by viewing the thermometer available via the /temp command.

**C** =====|===== **0** =====|===== **H**

Ideally, the player should be at 0, or within the first segment on either side.  If they player is in the second
segment on either side, they are either freezing (level = COLD) or overheating (level = HOT).  If they remain in
this temperature level for too long, they will start experiencing symptoms which will worsen the longer the player
remains.  Each segment on the thermometer is equal to 1, so -6 or 6 would be into the second segment, while -5 to 5
would keep the player in the NORMAL temperature level.

If overheating, the player will receive damage, the sweating effect and their thirst bar will drop quickly.
If freezing, the player will become slower and receive damage.

Temperature is calculated using a various sources as listed below:

**Core temperature:** 

This starts at 0.  Wearing armor can apply a modifier to either hot or cold:

Leather or chainmail armor will add 0.5 modifier for each piece being worn. A full set of this will apply (4 * 0.5) to
the core temperature, resulting in +2 (6+ would cause overheating)

Iron or gold armor will add 1 modifier for each piece being worn.  A full set of this will apply (4 * 1) to the core
temperature, resulting in +4 (6+ would cause overheating, so leaves little room for other modifiers)

Diamond armor would subtract 0.5 modifier for each piece being worn.  A full set of this would apply (4 * -0.5) to the
core temperature, resulting in -2 (-6 would cause freezing)

Netherite armor is considered perfectly neutral and will not apply a modifier.

Core temperature can also be modified if the player is underneath a block or exposed to the sky.  Being under a block
(considered as being in shelter) will normalize a players core temperature by 1, so if cold, the core temperature
will increase by 1, if hot, it will decrease by 1).

If a player is on fire, they will also get an extreme core temperature modifier of +10 (but probably have more 
important things on their mind)

**Ambient temperature:**

Ambient temperature starts at 0 and can be modified by the players surroundings;

Biome can provide a modifier based on time of day as outlined below, any not listd is 0:
> Badlands (and variants) = +3 during day, +4 around noon, -1 at night
> Beach = +1
> Cold Ocean = -1 during day, -0.5 around noon, -2 at night
> Deep Cold Ocean = -2.5 during day, -3 around noon, -3.5 at night
> Deep Frozen Ocean = -2.5 during day, -3 around noon, -3.5 at night
> Deep Lukewarm Ocean = -2.5 during day, -3 around noon, -3.5 at night
> Deep Ocean = -2.5 during day, -3 around noon, -3.5 at night
> Desert = +2 during day, +3 around noon, -1 at night
> Desert Hills = +2
> Desert Lakes = +2
> Frozen Oceans = -2.5
> Frozen Rivers = -2.5
> Ice Spikes = -2.5 during day, -3 around noon, -3.5 at night
> Jungle (and variants) = +1 during day, +1.5 around noon, +2.5 at night
> Lukewarm Ocean = +1
> Mountains (and variants) = -1
> Nether = +4
> River = -0.5
> Snowy Beach = -3
> Snowy Mountains = -3
> Snowy Taiga = -3
> Swamp = +1.5
> Taiga (and variants) = -1
> The End (and variants) = -4
> The Void = -10
> Warm Oceans = +1.5
> Wooded Badlands = +1

Elevation can provide a modifier.  Being too high can be cold, being too low can be too hot.  Based on players
Y location.

> 150+ = -2
> 81-150 = -1
> 31-80 = 0
> 16-30 = -1
> <= 15 = +1   (gets warmer due to being close to planet core)

Nearby blocks - Looks at blocks in a 2 block radius around the player, each block in that radius can provide a 
modifier.  Blocks with 1 block radius provides a more extreme modifier.  Nearby block modifiers has an upper and lower 
limit of 10 and -10 respectively.  Modifiers are cumulative.  So being within 1 block of lava would be +2, but
also being within 1 block of more lava would be +4

> Lava - Distance of 1 = +2, Distance of 2 = +1
> Fire - Distance of 1 = +2, Distance of 2 = +0.5
> Campfire (if lit) - Distance of 1 = +5, Distance of 2 = +0.5  (useful for warming up in a cold biome)
> Furnace, smoker or blast furnace (if lit) - Distance of 1 = +2, Distance of 2 = +1
> Blue ice, packed ice, frosted ice, snow block - Distance of 1 = -1.5, Distance of 2 = -0.5
> Ice - Distance of 1 = -0.75, Distance of 2 = -0.25
> Snow - Distance of 1 = -0.1, Distance of 2 = -0.05

**Ailment temperature:**

Ailments can have temperature modifiers, these are determined by the ailment configuration.  If you have that 
ailment, the modifier will be applied.

**Ailment Relief:**

Remedies can include x number of seconds of hot or cold relief.  When this relief is applied, the player will not
suffer any symptoms from either overheating or freezing (depending on the relief type)