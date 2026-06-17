# Elow Product Design

Date: 2026-05-24

## 1. Product Positioning

Elow is a toy-collectible style app for recording sugar and alcohol intake.
It helps users log real intake with low-pressure, object-based interactions,
then turns long-term reduction into collectible visual proof.

Elow is not a medical diagnosis app, a punishment dashboard, or a strict
abstinence tracker. It should feel like a playful recording tool with a
strong collection loop.

Core promise:

> Record what you consumed. See what you lowered. Collect the proof over time.

## 2. Target Behavior

Elow focuses on high-immediate-reward intake:

- Sugar drinks, especially cola and milk tea.
- Alcohol, especially beer and wine.

The first version records actual consumption, not only successful restraint.
This keeps the data honest and avoids making the app depend on perfect user
behavior.

## 3. Core Product Loop

Primary loop:

1. Open Elow.
2. Tap the center `+`.
3. Enter a full-screen object recording stage.
4. Swipe the top object strip to choose cola, milk tea, beer, or wine.
5. Adjust the large object directly by sliding the bottle/cup/glass level.
6. Save the record.
7. Return to Home and see today updated.
8. Over time, Me grows the honor wall.

Long-term loop:

1. Records accumulate.
2. Intake is compared against the user's own goals.
3. Lower intake unlocks honor wall objects, badges, and stage memories.
4. Monthly and yearly summaries turn reduction into high-impact visuals.

## 4. Main Navigation

The first version has only three main entries:

- `Home`
- `+`
- `Me`

No separate tabs for goals, review, settings, or honor wall. These are folded
into Home and Me.

### 4.1 Home

Home is about today only.

Home includes:

- Today status for sugar and alcohol.
- Recent records from today.
- Light goal progress.
- Short, low-pressure feedback.

Home does not contain the full honor wall or deep analytics. Its job is to make
the app immediately useful and keep the path to recording short.

### 4.2 Add

The center `+` opens the main recording experience.

Add is not a grid of equal item buttons. It is a full-screen object stage:

- Top area: horizontally scrollable small object icons.
- Main area: one large interactive object.
- Bottom area: current amount and save action.

Object interactions:

- Cola: slide the liquid level in the bottle.
- Milk tea: slide the cup level, with optional sweetness metadata later.
- Beer: slide can/glass level.
- Wine: slide wine glass level.

The user should feel like they are manipulating a toy object, not filling out a
form.

### 4.3 Me

Me is the personal and long-term center.

Me includes:

- Honor wall.
- Goal settings.
- Reference advice.
- Weekly and monthly review.
- Settings and data controls.

The honor wall is the most important Me feature.

## 5. First-Version Item Scope

The first version covers four items:

- Cola.
- Milk tea.
- Beer.
- Wine.

These four cover the core Elow positioning: sugar and alcohol. Other items
such as cake, chocolate, ice cream, cocktails, spirits, and snacks can be added
after the first version works.

## 6. Record Data Model

Each record stores:

- Item type: cola, milk tea, beer, or wine.
- Amount: object-specific amount from the interactive level.
- Timestamp: automatic, editable later.
- Estimated sugar, alcohol, calories, and money.
- Optional note, hidden by default.

The interface should not ask for grams, milliliters, or nutrition facts during
the first recording flow. Elow can estimate behind the scenes.

## 7. Goals And Reference Advice

User-defined goals are primary.

Examples:

- Weekly maximum number of sweet drinks.
- Weekly maximum number of drinking occasions.
- Daily or weekly reference limit for sugar and alcohol.

Reference advice is optional and appears only when the user asks for it. When a
user wants a more personalized reference, Elow can ask for body data such as
height, weight, age, sex, and activity level.

Do not ask for body data during first launch.

Reference advice guardrails:

- Sugar reference can be estimated from energy needs and added/free sugar
  guidance.
- Alcohol reference should use standard-drink guidance rather than pretending
  body size can make alcohol risk disappear.
- Advice is informational and should not be framed as diagnosis or treatment.

Useful public references:

- FDA added sugars overview: https://www.fda.gov/food/nutrition-facts-label/added-sugars-nutrition-facts-label
- WHO healthy diet guidance: https://www.who.int/news-room/fact-sheets/detail/healthy-diet
- CDC moderate alcohol use: https://www.cdc.gov/alcohol/about-alcohol-use/moderate-alcohol-use.html
- CDC standard drink sizes: https://www.cdc.gov/alcohol/standard-drink-sizes/index.html

## 8. Language Rules

Elow should avoid shame language.

Avoid:

- Failed.
- Broke the streak.
- Over limit.
- Bad day.

Prefer:

- Close to your target.
- A little high today.
- You recorded clearly.
- Tomorrow can be lighter.
- This week is lower than last week.

The app should make honest recording feel like progress.

## 9. Honor Wall

The honor wall is the long-term reward system.

Daily and weekly honor wall:

- Feels collectible.
- Shows objects, badges, stage memories, and small trophies.
- Uses toy-collectible visual language.

Monthly and yearly honor wall:

- Adds impact.
- Shows reduced cola bottles, milk tea cups, beer cans, wine glasses, sugar
  cubes, and saved money as large visual collections.
- Creates a shareable sense of progress without medical claims.

The honor wall should make lower intake visible and emotionally rewarding.

## 10. Visual Direction

Confirmed visual direction: light-shadow toy collectible.

Selected prototype:

- `docs/prototypes/style-sets/elow-style-06-light-shadow.png`

This style is the source of truth for the first implementation pass. It keeps
the toy-collectible product idea, but presents it through a lighter, more
modern visual system.

Style traits:

- Large areas of white and very pale gray space.
- Soft studio lighting, gentle realistic shadows, and shallow physical depth.
- Frosted acrylic or translucent cards instead of heavy paper, wood, or arcade
  machine frames.
- Drink items as clean miniature product sculptures, not flat vector icons.
- A few saturated accents per screen, such as blue for primary action and red
  for wine or alerting object state.
- Strong full-screen Add object stage with a large tactile drink object, visible
  fill level, and a physical-feeling drag handle.
- Minimal honor wall as a bright display cabinet with collectibles, badges,
  sugar cubes, saved-money tokens, and stage memories.

Guardrail:

- Keep the interface light, airy, and shadow-rich. Avoid returning to the
  heavier wood-shelf, scrapbook, clay, or arcade-machine treatments unless a
  later redesign explicitly chooses them.
- Avoid hard flat vector-icon styling. Functional icons can exist, but the main
  visual language should come from objects, light, shadow, and tactile surfaces.
- Keep goal and advice language calm so Elow does not become a punishment
  dashboard.

## 11. First-Use Flow

First launch should be short:

1. Open Elow.
2. Show a light introduction.
3. Let the user choose a broad direction: reduce sweet drinks, reduce alcohol,
   or reduce both.
4. Enter Home.
5. Let the first `+` action teach the full-screen object recording flow.

Do not require:

- Login.
- Body data.
- Detailed goals.
- Full onboarding survey.

## 12. Out Of Scope For First Version

The first version does not include:

- Full nutrition database.
- Barcode scanning.
- Medical diagnosis.
- Complex meal logging.
- Social feed.
- Virtual character or avatar system.
- Strict abstinence program.
- Large item catalog.

## 13. Open Product Questions

These are intentionally left for the next design or implementation phase:

- Exact visual style of each object.
- Exact object amount mapping, such as what 50 percent cola means in grams of
  sugar.
- Whether honor wall rewards are deterministic, random drops, or both.
- Whether data stays local only or syncs later.
- Whether reminders exist in the first version.

## 14. Acceptance Criteria For First Version

The first useful version should allow a user to:

1. Open Home.
2. Tap `+`.
3. Swipe between cola, milk tea, beer, and wine.
4. Adjust one full-screen object amount.
5. Save the record.
6. See the record reflected on Home.
7. Set a simple sugar or alcohol goal from Me.
8. See honor wall progress in Me.

If these are complete, Elow has its core product loop.
