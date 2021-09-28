(ns enviable.component-test
  (:require [clojure.test :refer :all]
            [enviable.component :as sut]
            [enviable.core :as env]
            [com.stuartsierra.component :as component])
  (:import (com.stuartsierra.component SystemMap)))

(defrecord Component [var-name]
  sut/Configurable
  (configuration [this]
    (env/var var-name))
  component/Lifecycle
  (start [this]
    (-> this
        (assoc :started true)))
  (stop [this]
    this))

(defn system-to-map [s]
  (if (instance? SystemMap s)
    (->> s
         (map (fn [[k v]]
                [k (system-to-map v)]))
         (into {}))
    s))

(deftest configure-system-test
  (let [environment {"A" "val-a"
                     "B" "val-b"
                     "C" "val-c"}]
    (testing "can configure a system"
      (is (= {:a (map->Component {:config "val-a" :var-name "A" :started true})
              :b (map->Component {:config "val-b" :var-name "B" :started true})
              :c (map->Component {:config "val-c" :var-name "C" :started true})}
             (-> (component/system-map
                   :a (Component. "A")
                   :b (Component. "B")
                   :c (Component. "C"))
                 (sut/configure-system environment)
                 component/start-system
                 system-to-map))))

    (testing "can configure system with a nested sub-system"
      (is (= {:a   (map->Component {:config "val-a" :var-name "A" :started true})
              :sub {:b (map->Component {:config "val-b" :var-name "B" :started true})
                    :c (map->Component {:config "val-c" :var-name "C" :started true})}}
             (-> (component/system-map
                   :a (Component. "A")
                   :sub (component/system-map :b (Component. "B")
                                              :c (Component. "C")))
                 (sut/configure-system environment)
                 component/start-system
                 system-to-map
                 ))))
    (testing "can drop in ad-hoc env vars into system"
      (is (= {:a (map->Component {:config "val-a" :var-name "A" :started true})
              :b "val-b"}
             (-> (component/system-map
                   :a (Component. "A")
                   :b (env/var "B"))
                 (sut/configure-system environment)
                 component/start-system
                 system-to-map))))))