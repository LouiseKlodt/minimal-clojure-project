(defproject minimal-clojure-project "0.1.0-SNAPSHOT"
  :description "Minimal Clojure Project"
  :url "https://github.com/LouiseKlodt/minimal-clojure-project"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.async "1.6.681"]
                 ;[ring/ring-jetty-adapter "1.10.0"]
                 ;[hiccup "1.0.5"]
                 ;[compojure "1.7.0"]
                 [com.taoensso/timbre "6.2.2"]]
  :main ^:skip-aot minimal-clojure-project.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[nrepl "1.0.0"]
                                  [midje "1.10.9"]
                                  [pjstadig/humane-test-output "0.11.0"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-cloverage "1.2.4"]
            [jonase/eastwood "1.4.0"]
            [lein-midje "3.2.2"]]
  :aliases {"test" ["midje"]
            "unit-test" ["midje" "unit.minimal-clojure-project.*"]})
