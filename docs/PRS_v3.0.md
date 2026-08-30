# P.R.S. v3.0 — Proximity Reconnaissance System

P.R.S. v3.0 is a BLE proximity-recognition system for the Galaxy A56. It
observes nearby BLE contacts, retains a short RSSI history and exposes the
change in signal over time. It is not a positioning system.

## Active menu

The P.R.S. menu exposes:

- `LOCAL SCAN`: A56 BLE scanning only.
- `SCAN + PROBE`: A56 scanning plus the executable Watch 2 PROBE node.
- `DEVICES`: device identification and persistent omission rules.
- `INDIVIDUAL TRACKER`: experimental A56-only tracking over a selected
  TERRAIN field.
- `OPERATION GUIDE`: deliberately empty; no physical field procedure is in the
  current scope.

The `prsOnlyDebug` edition is the compact cover-screen surface for the Z Flip
6. It centers `P.R.S.`, keeps the radar on the left and places the names-only
contact list on the right. It starts in `SCAN` and does not expose `SCAN +
PROBE`.

The old synthetic-position, two-pass survey and separate diagnostics flows are
not part of the active architecture.

## Acquisition and data layers

The active pipeline is:

`BLE SCAN -> CONTACTS -> RSSI PER CONTACT -> TEMPORAL HISTORY ->
SMOOTHING -> TREND -> DENSITY GRID`

Measured and inferred information remain separate:

| Layer | Current data |
| --- | --- |
| Measured | `BleObservation`: technical identifier, raw RSSI, elapsed timestamp, epoch timestamp, advertised name, advertising bytes, Android Bluetooth class, address type and source node. |
| Processed | `PrsProcessedSignal`: smoothed RSSI, temporal mean, variation and `RssiHistoryPoint` list. |
| Inferred | `PrsInference`: trend, relative proximity band, explanation and `DensityCloud`. |

The scanner uses the BLE address as the primary technical identifier when
Android provides one. The advertised name is display data and a fallback rule,
not the internal identity. If no usable name exists, the tracker assigns a
session-local label such as `UNKNOWN 01`.

## Temporal analysis

`PrsContactTracker` receives scan callbacks but evaluates a contact only at the
configured cadence. A single raw callback therefore cannot create an immediate
large change in the UI.

For each evaluated sample the tracker:

1. stores the latest raw RSSI and both timestamps;
2. applies exponential smoothing;
3. appends a processed point to the recent history;
4. compares the oldest and newest smoothed values in the window;
5. applies confirmation counts and hysteresis before changing the state.

The signal states are:

- `APPROACHING`: smoothed intensity increases sufficiently and remains
  confirmed over time;
- `STABLE`: the window variation remains below the significant threshold;
- `MOVING AWAY`: smoothed intensity decreases sufficiently and remains
  confirmed over time;
- `WAITING`: the contact has not accumulated enough temporal evidence.

The current provisional configuration in `PrsTuning.DEFAULT` is:

| Parameter | Initial value | Purpose |
| --- | ---: | --- |
| Evaluation cadence | 3 s | Maximum rate for processed samples. |
| Smoothing alpha | 0.35 | Exponential RSSI smoothing. |
| History window | 8 samples | Recent evaluated points retained. |
| Minimum trend samples | 4 | Evidence required before a trend can be inferred. |
| Minimum trend duration | 9 s | Minimum temporal span of the trend window. |
| Significant variation | 4.5 dB | Minimum change for approaching/away evidence. |
| Stable variation | 2.0 dB | Change treated as stable. |
| Hysteresis | 1.5 dB | Extra change required when leaving a trend. |
| Trend confirmations | 2 evaluations | Confirmation for approaching/away. |
| Stable confirmations | 2 evaluations | Confirmation for stable. |
| Contact expiry | 15 s | Time without a new observation before removal. |
| Near threshold | -76 dBm | Relative display band threshold. |
| Medium threshold | -88 dBm | Relative display band threshold. |

These are provisional relative-display defaults. They are not a universal RSSI
to distance conversion and are not presented as physically calibrated values.

## CONTACT LIST and TRACK TARGET

`CONTACT LIST` shows all active contacts currently retained by the tracker.
Each row may show the advertised name or an `UNKNOWN` session label, source,
raw RSSI, smoothed RSSI, relative proximity and trend. When the available BLE
evidence supports a category, the displayed name also receives one of
`[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` or `[COMPUTER]`. An unidentifiable
contact receives no category suffix.

Selecting a row enters `TRACK TARGET`. The selected target is highlighted in
amber in the GRID and its panel exposes:

- display name and technical identifier;
- current raw RSSI and smoothed RSSI;
- `APPROACHING`, `STABLE`, `MOVING AWAY` or `WAITING`;
- `NEAR`, `MEDIUM`, `FAR` or `UNKNOWN` as a relative band;
- recent RSSI history, variation and the explanation produced by the tracker.

Other contacts continue to be scanned and evaluated. Selecting the active row
again or using `STOP TRACKING` clears only the selected target. `CLEAR
CONTACTS` clears the whole in-memory session.

## DEVICES

### Device category inference

P.R.S. derives a practical device category from the advertised name first,
then Android's Bluetooth class and the BLE Appearance field. This is a
best-effort inference for quick reading, not a definitive manufacturer/model
identification. No confidence margin or question mark is shown. The same
category suffix is used by the full PIP-SuriOS surface and by `prsOnlyDebug`.

`DEVICES` is divided into three screens:

### IDENTIFY DEVICE

This screen runs a live BLE scan and exposes the information available for each
observation, including name, address, address type, raw RSSI and advertising
data preview. `SAVE DEVICE` stores the address as the strongest rule and keeps
the observed name as a friendly display name when available.

Manual entry accepts either a normalized MAC address or an exact advertised
BLE name. Name rules are useful when an address is private or rotating, but a
name may match more than one physical device.

### MAC ADDRESS GUIDE

To identify a device for the known-device list, power it on, enable Bluetooth,
keep it near the A56, open `IDENTIFY DEVICE`, and wait for an advertisement.
Verify the announced name, RSSI, and technical identifier before using `SAVE
DEVICE`. When Android exposes it, the identifier is the observed BLE address.
An address can also be entered manually as twelve hexadecimal characters, for
example `AA:BB:CC:DD:EE:FF`; colons and hyphens are accepted by the input.

Some devices use private or rotating BLE addresses. In that case, save the
exact advertised BLE name as a fallback, remembering that a name can match
multiple devices. An enabled rule hides matching observations, so disable the
rule in `SAVED DEVICES` before selecting that device as an `INDIVIDUAL TRACKER`
target.

### SAVED DEVICES

Each saved item has a persistent state:

- `ENABLED`: matching observations are omitted from `LOCAL SCAN` and
  `SCAN + PROBE` processing;
- `DISABLED`: the rule remains saved but matching observations are visible;
- `REMOVE`: deletes the saved rule.

## INDIVIDUAL TRACKER

`INDIVIDUAL TRACKER` is an experimental feature dependent on `P.R.S.` and
`TERRAIN`, with no reverse dependency. It uses only the A56 `LOCAL SCAN`; it
does not use `PROBE` or `SCAN + PROBE`.

In `TARGET`, choose the TERRAIN field first and then one detected contact. The
known-device rules from `DEVICES` are respected. In `TRACKER`, the selected
TERRAIN map is shown with the P.R.S. GRID centered on the A56's current GPS
position, and only the selected contact's signal is displayed. The GRID is not
a target coordinate, bearing, distance, or RSSI-to-meters conversion. A future
uncertainty-circle model will require physical test data and is intentionally
not enabled yet.

The registry stores rules independently from the contact display name and
migrates the previous omission-rule storage once. It does not use a device
name as the primary identity when a technical address is available.

## GRID and density clouds

The visual GRID style from P.R.S. v2.0 is retained: grid cells, scanlines,
concentric rings, corner brackets and the Brotherhood watermark. Its former
point-placement and synthetic-angle logic is removed.

Each contact is rendered as a diffuse annular density cloud. Signal strength
selects a relative radial band, while the cloud spans the full azimuth. This
communicates uncertainty: a single BLE receiver knows that a signal exists and
can compare its relative strength, but it does not measure bearing, X/Y,
metres or exact position.

The `PrsDensityEstimator` interface is the extension point for future density
models. It accepts an optional `PrsMovementContext`, but the current default
estimator intentionally ignores movement and keeps full azimuth uncertainty.

When `SCAN + PROBE` is active, a valid Watch 2 location is drawn as a small
subgrid inside the main GRID. This is the relative display of the two receiver
nodes, not a BLE direction and not the location of a contact. It is withheld
when a required fix is missing and is marked stale when its age exceeds the
display limit.

## Watch 2 PROBE integration

The executable Wear OS `:probe` module is a headless sensor node. Its visible
surface reports operational state and does not render contact telemetry. It
scans BLE and provides location/status packets through the shared
`:probeprotocol` Data Layer contract.

The phone-side `ProbeLink` sends control commands through Wearable, while
`ProbeDataLayerService` receives persisted telemetry and forwards it to
`ProbeTelemetryStore`. The phone converts remote BLE samples into the same
`BleObservation` model used by the A56, marks their source as `PROBE_WATCH_2`
and sends them through the same tracker and device filters.

## Diagnostics and observability

There is no separate `DIAGNOSTICS` menu. The diagnostic surface is integrated
into `CONTACT LIST` and `TRACK TARGET`, which expose the values needed to
explain a decision: raw RSSI, smoothed RSSI, timestamps, history, variation,
trend, proximity and the tracker's explanation.

## Future extension boundary

`PrsMovementContext` and `PrsDensityEstimator` reserve the boundary for later
accelerometer, gyroscope, magnetometer, heading and displacement experiments.
Those experiments are not scheduled in the current scope. Any future
refinement must remain an inference layer and must not be written as a directly
measured BLE direction.

## Explicitly out of scope

The active implementation does not provide:

- RSSI-to-metres calibration as an exact measurement;
- real X/Y coordinates or BLE bearing;
- Wi-Fi RTT, UWB, triangulation or machine learning;
- definitive device classification independent of the data advertised by a
  nearby device;
- a definitive spatial-cloud reduction algorithm.

RSSI varies with device hardware, orientation, obstacles, propagation,
transmit power and interference. `APPROACHING`, `STABLE` and `MOVING AWAY`
describe signal evolution and are not proof that a physical target moved.
