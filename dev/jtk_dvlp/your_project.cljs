(ns ^:figwheel-hooks jtk-dvlp.your-project
  (:require
   [goog.dom :as gdom]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [jtk-dvlp.re-frame.dragndrop :as dnd]))


(defn app-view
  []
  [:<>
   [:style {:type "text/css"}
    ".draggable {
       cursor: pointer;
       display: inline-block;
       padding: 15px;
     }

     .draggable.draggable--dragging {
       opacity: 0.5;
     }

     .dropzone {
       padding: 15px;
       border: 1px dotted black;
       margin: 5px;
     }

     .dropzone.dropzone--over {
       border-color: green;
     }"]

   ;; creates a draggable div with custom type and edn data
   [dnd/draggable
    {:type :my-entity, :data {:yeah :my-entity-data}}
    [:div "drag some entity data"]]

   ;; creates a dropzone div to allow jpg files
   [dnd/dropzone
    {:types "image/jpeg", :on-drop (comp console.debug clj->js)}
    [:div "drop files here"]]

   ;; creates a dropzone div to allow custom type as defined
   [dnd/dropzone
    {:types :my-entity, :on-drop (comp console.debug clj->js)}
    [:div "drop edn here"]]])


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; re-frame setup

(defn- mount-app
  []
  (rdom/render
    [app-view]
    (gdom/getElement "app")))

(defn ^:after-load on-reload
  []
  (rf/clear-subscription-cache!)
  (mount-app))

(defonce on-init
  (mount-app))
