[![Clojars Project](https://img.shields.io/clojars/v/jtk-dvlp/re-frame-dragndrop.svg)](https://clojars.org/jtk-dvlp/re-frame-dragndrop)
[![cljdoc badge](https://cljdoc.org/badge/jtk-dvlp/re-frame-dragndrop)](https://cljdoc.org/d/jtk-dvlp/re-frame-dragndrop/CURRENT)

# re-frame-dragndrop

re-frame view components for drag & drop.

## Features

* make views draggable
  * clojure data (edn)
  * any mime-type
* make views to dropzones
  * clojure data (edn)
  * any mime-type e.g. files
* no extra state just native api

## Getting started

### Get it / add dependency

Add the following dependency to your `project.clj`:<br>
[![Clojars Project](https://img.shields.io/clojars/v/jtk-dvlp/re-frame-dragndrop.svg)](https://clojars.org/jtk-dvlp/re-frame-dragndrop)

### Usage

See in repo [your-project.cljs](https://github.com/jtkDvlp/re-frame-dragndrop/blob/master/dev/jtk_dvlp/your_project.cljs)

```clojure
(ns jtk-dvlp.your-project
  (:require
   ...
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
```

## Appendix

I´d be thankful to receive patches, comments and constructive criticism.

Hope the package is useful :-)
