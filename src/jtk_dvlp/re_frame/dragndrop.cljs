(ns jtk-dvlp.re-frame.dragndrop
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [jtk-dvlp.transit :as transit]))


(def ^:private !clj->transit (atom nil))
(def set-fn-clj->transit!
  (partial reset! !clj->transit))

(defn- clj->transit
  [clj]
  (let [f (or @!clj->transit transit/clj->transit)]
    (f clj)))

(def ^:private !transit->clj (atom nil))
(def set-fn-transit->clj!
  (partial reset! !transit->clj))

(defn- transit->clj
  [transit]
  (let [f (or @!transit->clj transit/transit->clj)]
    (f transit)))

(defn- ensure-coll
  [x]
  (if (coll? x)
    x
    (vector x)))

(defn- normalize-type
  [type]
  (let [edn? (not (string? type))]
    {:edn? edn?
     :string (cond-> type edn? (clj->transit))
     :raw type}))

(defn- normalize-types
  [types]
  (->> types
       (ensure-coll)
       (map normalize-type)
       (map (juxt :string identity))
       (into {})))

(defn- set-transfer-data!
  [js-event {:keys [edn?] :as type} data]
  (let [transfer
        (aget js-event "dataTransfer")

        data
        (cond-> data
          edn?
          (clj->transit))]

    (js-invoke transfer "setData" (:string type) data)))

(defn- get-transfer-items
  [js-event types]
  (->> (aget js-event "dataTransfer" "items")
       (array-seq)
       (filter (comp types #(aget % "type")))
       (seq)))

(defn- get-transfer-data
  [js-event types]
  (for [item (get-transfer-items js-event types)
        :let [kind (aget item "kind")
              type (aget item "type")
              edn? (get-in types [type :edn?])]]
    (case kind
      "string"
      (let [transfer (aget js-event "dataTransfer")]
        (cond-> (js-invoke transfer "getData" type)
          edn?
          (transit->clj)))

      "file"
      (js-invoke item "getAsFile"))))

(defn- can-handle-transfer-data?
  [js-event types]
  (->> types
       (get-transfer-items js-event)
       (some?)))

(defn- prevent-drop!
  [js-event]
  (aset js-event "dataTransfer" "dropEffect" "none"))

(defn- prevent-default!
  [js-event]
  (js-invoke js-event "preventDefault"))

(defn- normalize-element
  [[tag attrs-or-content & more-content :as element]]
  (let [[attrs content]
        (if (map? attrs-or-content)
          [attrs-or-content more-content]
          [{} (into [attrs-or-content] more-content)])]

    (into [tag attrs] content)))

(defn- normalize-class
  [class]
  (cond
    (vector? class)
    class

    (nil? class)
    []

    :else [class]))

(defn draggable
  "Modifies `element` to be draggable.

  - `type` defines the type of data to transfer via drag & drop. Supports string (e.g. mimetype for exchange of files), keyword and other values working with `jtk-dvlp.transit/clj->transit`. Need to match dropzone`s type to be droppable.
  - `data` defines the data to transfer via drag & drop. Supports strings and any value working with `jtk-dvlp.transit/clj->transit`. Auto converts non string values to transit-json and vice versa, when `type` is not a string assuming non mimetype value but clojure structures.
  - `on-drag` (optional) defines a re-frame event vector or function to dispatch / call with the `data` on start dragging

  See `dropzone`."
  [{:keys [type data on-drag] :as attrs} element]
  (let [!dragging? (r/atom false)]
    (fn [{:keys [type data on-drag]} element]
      (let [[tag {:keys [class] :as attrs} & content]
            (normalize-element element)

            class
            (cond-> (normalize-class class)
              :always
              (conj :draggable)

              @!dragging?
              (conj :draggable--dragging))

            type
            (normalize-type type)

            on-drag
            (if (vector? on-drag)
              #(rf/dispatch (conj on-drag %))
              on-drag)

            drag-attrs
            {:class class
             :draggable true

             :on-drag-start
             (fn [js-event]
               (set-transfer-data! js-event type data)
               (reset! !dragging? true)
               (when on-drag
                 (on-drag data)))

             :on-drag-end
             (fn [_js-event]
               (reset! !dragging? false))}]

        (into [tag (merge attrs drag-attrs)] content)))))

(defn dropzone
  "Modifies `element` to function as dropzone for draggables.

  - `types` defines the type or collection of types of data to be allowed dropping over this element / zone. Supports string, keyword and other values working with `jtk-dvlp.transit/clj->transit`. Need to match draggable´s types to allow dropping.
  - `on-drop` defines a re-frame event vector or function to dispatch / call with a vector of the dropped data on dopping a draggable element.

  See `draggable`."
  [{:keys [types on-drop] :as attrs} element]
  (let [!over? (r/atom false)]
    (fn [{:keys [types on-drop]} element]
      (let [[tag {:keys [class] :as attrs} & content]
            (normalize-element element)

            class
            (cond-> (normalize-class class)
              :always
              (conj :dropzone)

              @!over?
              (conj :dropzone--over))

            types
            (normalize-types types)

            on-drop
            (if (vector? on-drop)
              #(rf/dispatch (conj on-drop %))
              on-drop)

            drop-attrs
            {:class class

             :on-drag-enter
             (fn [js-event]
               (when (can-handle-transfer-data? js-event types)
                 (reset! !over? true)))

             :on-drag-over
             (fn [js-event]
               (if (can-handle-transfer-data? js-event types)
                 (prevent-default! js-event)
                 (prevent-drop! js-event)))

             :on-drop
             (fn [js-event]
               (when-let [data (get-transfer-data js-event types)]
                 (prevent-default! js-event)
                 (on-drop data)
                 (reset! !over? false)))

             :on-drag-leave
             (fn [js-event]
               (reset! !over? false))}]

        (into [tag (merge attrs drop-attrs)] content)))))
