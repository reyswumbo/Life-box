# LIFE INBOX — DESIGN SPECIFICATION

## 1. Design Vision

Life Inbox is a minimalist Android link-saving application
built around a friendly, customizable virtual pet.

The interface should feel calm, warm, playful, and personal.

The virtual pet must be the visual centerpiece of the Home
screen rather than a small decorative icon.

The application should combine the usefulness of a bookmark
manager with the emotional appeal of a virtual companion.

Create original visual assets inspired by the general design
direction described below.

Do not reproduce copyrighted characters or copy the exact
assets of existing applications.

## 2. Visual Language

Use a soft pastel visual system.

### Background

Use a warm off-white background.

Suggested starting color:
#F7F7F0

Avoid pure white across the entire interface because the
application should feel warm and slightly organic.

### Primary Accent

Use sage green.

Suggested color:
#A9C39A

Use it for primary buttons, selected tabs, active states,
and customization highlights.

### Secondary Colors

Suggested palette:

- Soft Sage: #A9C39A
- Pale Sage: #E7EEDD
- Warm Cream: #F7F4E8
- Light Lavender: #EFEDF6
- Soft Yellow: #F3E9B9
- Dark Text: #33382F
- Secondary Text: #85877D
- Surface White: #FFFDF7

These are initial design tokens, not rigid restrictions.

Maintain adequate contrast and readability.

## 3. Shape Language

Use:

- Rounded cards
- Large rounded input fields
- Softly rounded buttons
- Thin borders
- Subtle shadows
- Minimal outlines
- Small decorative illustrations

Avoid excessive gradients, strong shadows, neon colors,
and unnecessary visual complexity.

The application should remain visually clean.

## 4. Typography

Use a clear sans-serif typeface for general UI text.

Use a restrained serif typeface for selected editorial
headings if it improves the design.

Use consistent typography scales for:

- Page titles
- Section headings
- Body text
- Captions
- Labels
- Button text

Do not use decorative fonts for essential controls.

## 5. Main Navigation

Implement three primary tabs:

1. Home
2. Saved
3. Pet Lab

Use a floating-looking bottom navigation container
with rounded corners and a soft background.

The selected tab should have a pale sage background
and a clearly visible icon.

Unselected tabs should remain visually quiet.

Navigation must respect Android system insets.

The keyboard must not permanently obscure the navigation
or essential form controls.

## 6. Home Screen Layout

The Home screen should resemble a cozy, minimalist room.

### Header

Display:

LIFE INBOX

Use centered or carefully balanced letter spacing.

A small status indicator may appear beside the title.

### Collection Shelf

Near the top of the screen, display a compact shelf
containing collection cards.

Each card should show:

- Collection name
- Number of saved links
- Soft background color

Examples:

Ideas
Inspiration

Make the shelf horizontally scrollable when necessary.

Do not force collection cards into an inflexible layout
on small devices.

### Environmental Decorations

Create subtle room decorations.

Possible elements:

- A small window
- Soft ambient light
- A minimal plant
- A platform or floor opening
- Tiny paw-print decorations

Use original vector shapes or lightweight assets.

These elements must not compete with the pet.

They should be decorative rather than interactive unless
an explicit interaction is implemented.

### Virtual Pet

Position the pet prominently in the central area.

The pet should appear to emerge from a dark oval opening
or rest on a small platform.

Use layered drawing:

1. Ground shadow
2. Platform or opening
3. Pet body
4. Eyes
5. Mouth
6. Accessories
7. Optional texture overlay

The pet should have a recognizable silhouette.

Its eyes should be expressive and visible.

The pet must be large enough to remain recognizable
on small Android screens.

### Daily Statistics

Display a small card showing the number of links
saved during the current local calendar day.

Example:

YOUR SAVES
4
today

Calculate the number from persisted database records.

Do not store a counter that can become inconsistent
with the actual saved links.

### Pet Message

Display a short message beneath the pet.

Examples:

"Take your time."

"Ready for your next discovery?"

"Your little ideas are safe here."

Messages may change according to pet state.

### URL Input

Place a large rounded input field in the lower portion
of the Home screen.

The placeholder should read:

"Paste a link to save..."

Include:

- A paste-friendly text field
- A leading plus icon
- A clear submit button
- A destination collection selector

The input field should expand vertically when the text
wraps onto multiple lines.

When the keyboard appears, keep the input and submit
button accessible.

### Saving Interaction

When the user submits a valid URL:

1. Validate the URL.
2. Save the URL to the local database.
3. Start the pet's receiving animation.
4. Open the pet's mouth.
5. Animate the link card toward the pet.
6. Play the swallowing animation.
7. Update the daily statistics.
8. Display a success message.

The animation must not indicate success if the database
operation fails.

## 7. Saved Screen Layout

The Saved screen is the user's link library.

### Header

Display:

"Your Link Library"

Include a plus button for creating collections
or adding a saved item.

### Search

Provide a large rounded search field.

Placeholder:

"Search your saved links..."

Include a separate filter button.

Search should match:

- Link title
- URL
- Domain
- Notes

### Collection Cards

Display collection cards in a horizontally scrollable
row or responsive grid.

Default collections:

- All
- Inbox
- Ideas
- Inspiration

Each collection should have:

- A distinct soft background
- A collection icon
- A collection name
- A saved-link count
- A small decorative pet or mascot when appropriate

The All collection must be a computed view of all links.

It must not create duplicate link records.

### Link List

Display saved links as rounded cards.

Each card should contain:

- Website title
- Domain
- Optional thumbnail
- Collection label
- Date saved
- Favorite action
- Overflow menu

The overflow menu may provide:

- Open
- Edit
- Move
- Copy URL
- Delete

Thumbnails are optional.

A link must remain usable even when metadata or thumbnail
retrieval fails.

### Empty State

If the selected collection contains no links,
display a friendly empty state.

The pet may appear with a short message suggesting
that the user save their first link.

Do not make the empty state visually overwhelming.

## 8. Collection Management

Users must be able to:

- Create collections
- Rename collections
- Select an icon
- Select a color
- View collection contents
- Move links between collections
- Delete collections safely

Deleting a collection must not silently delete all
associated links.

Provide a clear destination policy when deleting
a collection that still contains links.

The Inbox collection should remain available
as the default destination.

## 9. Pet Lab Screen

The Pet Lab is the customization workspace.

### Preview

Display a large live preview of the current pet.

The pet should react gently when customization options
change.

Examples:

- Changing the body shape triggers a short bounce.
- Changing eye style triggers a blink.
- Changing color triggers a subtle reaction.
- Adding an accessory triggers a celebratory movement.

### Character Name

Provide a text field labeled:

"Character name"

The default name may be:

"Pocket Pal"

Allow users to choose their own name.

### Customization Categories

Use a horizontally scrollable category selector.

Categories:

- Body
- Eyes
- Accessories
- Colors
- Textures

### Body Options

Provide these shapes:

- Round
- Boxy
- Fluffy
- Triangle
- Heart

Each option should show a small preview.

The currently selected option must have a clear
visual selection indicator.

### Eyes

Provide several eye styles.

Examples:

- Normal
- Sleepy
- Happy
- Curious

Keep the eye rendering independent from the body.

### Accessories

Provide reusable accessory layers.

Examples:

- Crown
- Horns
- Hat
- Decorative items

Accessories must align correctly with the selected
body shape.

### Colors

Allow users to customize:

- Body color
- Eye color
- Accessory color when applicable

Use a curated palette for the initial release.

Avoid requiring a complicated color editor
unless it is needed.

### Textures

Provide a small selection of textures.

Examples:

- Smooth
- Fluffy
- Fuzzy

Reuse texture assets whenever possible.

Do not create a separate full-resolution image
for every combination of customization options.

### Save Character

Display a prominent button:

"Save Character"

Save the selected configuration locally.

The saved configuration must persist after the
application is closed and reopened.

## 10. Pet Animation Principles

The pet must not look like a static sticker.

Implement subtle, continuous idle behavior:

- Gentle floating
- Occasional blinking
- Small eye movements
- Slight body squash and stretch
- Occasional curious reactions

Use controlled animation intervals.

Do not run expensive rendering operations continuously
when the Home or Pet Lab screen is not visible.

### Animation States

Support:

IDLE
BLINKING
LOOKING
CURIOUS
RECEIVING
OPEN_MOUTH
EATING_LINK
SWALLOWING
CELEBRATING
SLEEPING
CONFUSED
CUSTOMIZING

The animation controller must prevent conflicting
animations from running simultaneously.

### Eating Animation

The eating animation is a core interaction.

The URL card should:

1. Move toward the pet.
2. Scale down gradually.
3. Disappear behind or inside the mouth.
4. Trigger a swallowing movement.
5. Cause a short celebratory reaction.

Use coordinated position, scale, mouth, and body animations.

Do not use an external video for this interaction.

## 11. Responsive Design

The interface must adapt to:

- Small Android phones
- Tall displays
- Different screen densities
- Portrait orientation
- Keyboard-visible states

Use responsive spacing and scrollable content.

Avoid hardcoded screen coordinates.

Use Compose layout primitives instead of positioning
every component with absolute pixel coordinates.

## 12. Accessibility

Provide:

- Meaningful content descriptions
- Accessible touch targets
- Readable text contrast
- Keyboard-friendly input behavior
- Clear validation messages
- Clear success and error states

Animation must not be the only way to communicate
the result of an operation.

Respect reduced-motion preferences where practical.

## 13. Performance

Use lightweight vector graphics and reusable components.

Prefer Canvas animations for simple geometric movement.

Use image assets only when they provide a meaningful
visual benefit.

Avoid unnecessary network requests.

The entire core experience must work offline.

Optimize release builds using R8 and resource shrinking.

Verify actual performance and APK size through builds
rather than relying on assumptions.

## 14. Design Acceptance Criteria

The design is complete when:

- The Home screen feels like a cozy room.
- The virtual pet is the visual centerpiece.
- The URL input is easy to access.
- The pet reacts when a link is saved.
- Saved links are organized into collections.
- Users can create custom collections.
- The Pet Lab provides a live preview.
- Customization persists after restarting.
- The layout works on small screens.
- Animations remain lightweight.
- The design is consistent across all three tabs.
