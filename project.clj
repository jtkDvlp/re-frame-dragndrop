(defproject net.clojars.jtkdvlp/re-frame-dragndrop "0.0.0-SNAPSHOT"
  :description
  "re-frame view components for drag & drop"

  :url
  "https://github.com/jtkDvlp/re-frame-dragndrop.git"

  :license
  {:name
   "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url
   "https://www.eclipse.org/legal/epl-2.0/"}

  :source-paths
  ["src"]

  :target-path
  "target"

  :clean-targets
  ^{:protect false}
  [:target-path]

  :dependencies
  [[org.clojure/clojure "1.10.0"]
   [org.clojure/clojurescript "1.10.773"]
   [net.clojars.jtkdvlp/transit "0.0.0-SNAPSHOT"]]

  :profiles
  {:provided
   {:dependencies
    [[re-frame "1.1.2"]
     ,,,]}

   :dev
   {:dependencies
    [[com.bhauman/figwheel-main "0.2.13"]]

    :source-paths
    ["dev"]}

   :repl
   {:dependencies
    [[cider/piggieback "0.5.2"]]

    :repl-options
    {:nrepl-middleware
     [cider.piggieback/wrap-cljs-repl]

     :init-ns
     user

     }}}

  ,,,)
