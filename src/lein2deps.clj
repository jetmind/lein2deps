(ns lein2deps
  (:require [rewrite-clj.zip :as z]
            [rewrite-clj.node :as n]
            [clojure.string :as str]))

(defn convert-single-dep [dep-node]
  (let [[art space ver & rest] (n/children dep-node)
        art                    (if (qualified-symbol? (n/sexpr art))
                                 art
                                 (symbol (str art "/" art)))]
    (n/vector-node [art space
                    (n/map-node
                      (into [(n/keyword-node :mvn/version) (n/spaces 1) ver]
                        rest))])))

(defn vector->map [deps]
  (->> (n/children deps)
       (mapcat #(if (= :vector (n/tag %))
                  (n/children %)
                  [%]))
       (n/map-node)))

(defn lein->deps [zloc]
  (-> (z/map #(z/edit* % convert-single-dep) zloc)
      (z/edit* vector->map)
      (z/string)))

(defn find-all-deps-declarations [zloc]
  (->> zloc
       (iterate #(z/find-next-value % z/next :dependencies))
       (take-while (complement z/end?))
       (keep z/right)))

(defn -main [& args]
  (->> (first args)
       (slurp)
       (z/of-string)
       (find-all-deps-declarations)
       (map lein->deps)
       (str/join "\n\n")
       (println)))
