# ChaosMod
A plugin where every 10 seconds, something would happen!
Inspired by [Chaos Mod V made by pongo1231](https://www.gta5-mods.com/scripts/chaos-mod-v-beta).
## Usage
Use these arguments with 
```
/chaos <argument>
```
You have a few options.
toggle: This toggles the state of the plugin. If its enabled it will be disabled and vice versa.

help: With no arguments, it shows this help message. However, if you put the id of an action, it will show the help file for this action.

run: Specify an id for an action to run it. See the help file for the action for parameters.

reload: This reloads the probabilities from chaosConfig.json if it was modified.

save: This creates a new chaosConfig.json using the probabilities in ram.

cancelTasks: This cancels all tasks in the plugin, in case you made a task run for years on accident.

spectators: Spectators don't have actions preformed on them. Specify spectators add <playername> to add and spectators remove <playername>. You can list the spectators 
  using spectators list.
disable and enable: Disable and enable the random actions respectively.

## Modifying parameters
  The parameters for every action is stored in a file called chaosConfig.json. If there isn't one, it will create one for you.
  You are able to format it using a plugin like [JSON Viewer for Notepad++](https://stackoverflow.com/a/5083037).
  ### Name
  This is what will display in chat when the action is triggered. If it's a timed action, then it will display ``` <Action> for <time> seconds ```
  ### Probability
  The actions are chosen randomly using a [min max algorithm](https://stackoverflow.com/a/6737362). Where it will add all probabilities, then multiply it by a random double from 0 to 1. Each action's probability is enumerated through until the final number is less than 0. The action that made it less than 0 is then chosen to be run.
  ### Time
  These are specific to timed actions. It's how long the action will execute for (approximatly, since you can only count in intervals of 1/20 of a second, or one tick).
  ### Delay
  The plugin will wait for a given amount of time until the next action is run.
  
  
  These are the general terms used for actions. Some actions will have seperate parameters. You can view them using ```/chaos help <ActionId> ```
  
  ## Ids
  Ids for the actions look like this: ``` ParentId.AnotherParent.Action ```
  Parent ids work similarly to folders. They exist because if you have similar actions, such as summoning an entity, it would saturate the other actions.
  An example of a real action is:
  ``` SpawnEntity.SpawnCow ```
  All you need to do to find an actions id is to look inside the chaosConfig.json file. Here is Spawn Cow:
  ``` "Spawn Entity": {
		"Actions": {
			"Spawn Cow": {
				"Probability": 2,
				"Name": "Spawn Cow"
			}
  ```
  The Associated strings are seperate from the Name parameter, since these display in chat.
  You can see that It goes Spawn Entity to Spawn Cow. Simply remove the spaces and there's your id. If this was too confusing, you can simply use ```/chaos help <id> ```
and it will autocomplete with an id.
## Running actions
Actions can run individually with different parameters.
Here are some:
### player
This specifies the player to execute this on. By default, this will execute on yourself. Specify @a for everyone on the server or a player name for an individual player.
### time
This specifies the time to execute it for if it's a timed action.
In order to execute actions with parameters you need to run:
``` /chaos run <Action Id> <Parameters> ```
So if I wanted to run Arrow Rain for 3 seconds and with a frequency of 1, I would run:
``` /chaos run ArrowRain -time 3 -frequency 1 ```
These can be in any order that you want too!
## Creating your own actions
I know I'm not the best at explaining things, so I won't go as deep here, but if you want to create your own action, then you have create a class that implements Action or RepeatingAction.
You're welcome to copy an existing action and modify it.
## doAction()
Do action is what the plugin will run when it's executing an action. Schedule a repeating async task if it's a repeating task.
Remember that async tasks can't access any API, such as spawning mobs, but you can create a new syncronous task to spawn it.
## save() and load()
Jsons work with a key value system, so if you save an object with the value of "Time", then you load "Time" in the load() function. Keep in mind the possibility of something being there that isn't supposed to. It'll create an error.
## invokeAction()
This is what is run when the action is ran individually. Use something like this:
```if(args.get("player").equals("@a")){
            // Run if it's everyone
            doAction();
        }else{
            // Have a similar thing happen but for an individual
        }
```
Again, copy from an existing action if you want.
