Zombie-Escape-Plan
==================

After one of our pet projects at HereLabs went wrong, we accidentally triggered the zombie apocalypse. Zombies are particularly slow in hilly terrain, so you and your fellow survivors figure Marin is probable the safest spot to be. To get there, all N survivors have to cross a particular bridge. Unfortunately you only brought one chainsaw, and crossing bridges without chainsaws is highly inadvisable during zombie attacks. Due to unrelated events, the bridge is also close to collapsing, and at most two people can safely cross the bridge at a time.

To make things more complicated, everybody needs a different time to cross the bridge. And running away from zombies is extremely fatiguing, so after each crossing of the bridge the time somebody needs to cross increases by 20%, while each minute of rest reduces the time somebody needs to cross by 5 seconds (until it reaches the person's initial value again). If two people are traveling together, the speed of the two is the speed of the slower survivor.

Your task is to find a strategy that minimizes the time needed by the entire group. Your input will be a file with N lines, each giving you the number of minutes a person needs to cross the bridge, e.g. for a group of 6 survivors:

20 15 18 27 12 30

Your program should return the total time your solution needs and the strategy in the following format

-> 0 1 (20) <- 1 (18) -> 2 5 (18) <- 0 5

Meaning that in the first crossing, Survivors 0 and 1 cross the bridge towards Marin and need 20 minutes. 0 keeps the chainsaw and returns to the Peninsula, but due to fatigue needs 18 minutes (15 + 20%) now. He passes your weapon of choice to 3 and 5, which cross the bridge in 18 minutes. 5 keeps the chain saw and returns together with 0. 5 would need 14.4 minutes to return now, but is slowed down by 0 who needs 21 minutes (20 * 1.2 = 24 min, then 36 minutes of rest = 180 seconds recovered, 20 - 180/60 = 21).

Again, use a programming language of choice; you can solve this problem arithmetically, using optimization techniques, or even using good heuristics. Bonus points for cute zombie visualizations.

