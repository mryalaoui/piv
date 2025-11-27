Feature: PVI simulation

  Scenario: Valider plusieurs scénarios de simulation PVI
    Given les scénarios PVI suivants :
      | id | typeBien    | usageBien            | origineBien | dateOrigine | dateVente  | prixOrigine | fraisOrigine | fraisReels | prixVente | fraisVente | travaux | montantTravaux |
      | 1  | MAISON      | RESIDENCE_PRINCIPALE | ACHAT       | 2015-03-15  | 2025-03-15 | 200000      | 0            | 0          | 300000    | 0          | NON     | 0              |
      | 2  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2021-01-01  | 2025-01-01 | 5000        | 0            | 0          | 14000     | 0          | NON     | 0              |
      | 3  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2021-01-01  | 2025-01-01 | 200000      | 0            | 0          | 150000    | 0          | NON     | 0              |
      | 4  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2021-09-10  | 2025-09-10 | 100000      | 0            | 0          | 147500    | 0          | NON     | 0              |
      | 5  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2021-09-10  | 2025-09-10 | 100000      | 0            | 0          | 167500    | 0          | NON     | 0              |
      | 6  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2015-03-15  | 2025-03-15 | 100000      | 0            | 0          | 200000    | 0          | NON     | 0              |
      | 7  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2002-07-01  | 2025-07-01 | 100000      | 0            | 0          | 250000    | 0          | NON     | 0              |
      | 8  | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 1990-01-01  | 2025-01-01 | 100000      | 0            | 0          | 300000    | 0          | NON     | 0              |
      | 9  | MAISON      | RESIDENCE_SECONDAIRE | ACHAT       | 2022-01-01  | 2025-01-01 | 100000      | 0            | 0          | 160000    | 0          | OUI     | 20000          |
      | 10 | MAISON      | RESIDENCE_SECONDAIRE | ACHAT       | 2017-01-01  | 2025-01-01 | 100000      | 0            | 0          | 172500    | 0          | OUI     | 8000           |
      | 11 | TERRAIN     | RESIDENCE_SECONDAIRE | ACHAT       | 2015-01-01  | 2025-01-01 | 100000      | 0            | 0          | 165500    | 0          | OUI     | 8000           |
      | 12 | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2004-01-01  | 2025-01-01 | 100000      | 0            | 0          | 250000    | 0          | NON     | 0              |
      | 13 | APPARTEMENT | RESIDENCE_SECONDAIRE | ACHAT       | 2004-01-01  | 2026-01-01 | 100000      | 0            | 0          | 250000    | 0          | NON     | 0              |

    And les résultats attendus pour chaque scénario :
      | id | plusValueBrute | plusValueNette | impotRevenu | prelevementsSociaux | taxePlusValue | impotsTotaux | netDispo | pressionFiscale | taxOutcomeState                        |
      | 1  | 55000          | 0              | 0           | 0                   | 0             | 0            | 300000   | 0               | NON_IMPOSABLE_RESIDENCE_PRINCIPALE     |
      | 2  | 8625           | 0              | 0           | 0                   | 0             | 0            | 14000    | 0               | NON_IMPOSABLE_SEUIL_PRIX_VENTE         |
      | 3  | -65000         | 0              | 0           | 0                   | 0             | 0            | 150000   | 0               | NON_IMPOSABLE_MOINS_VALUE              |
      | 4  | 40000          | 40000          | 7600        | 6880                | 0             | 14480        | 133020   | 36              | IMPOSABLE_IR_PS                         |
      | 5  | 60000          | 60000          | 11400       | 10320               | 1200          | 22920        | 144580   | 38              | IMPOSABLE_IR_PS_SURTAXE                |
      | 6  | 77500          | 54250          | 10308       | 12230               | 798           | 23336        | 176664   | 30              | IMPOSABLE_IR_PS_SURTAXE                |
      | 7  | 127500         | 0              | 0           | 13816               | 0             | 13816        | 236184   | 11              | IMPOSABLE_PS_SEULEMENT                  |
      | 8  | 177500         | 0              | 0           | 0                   | 0             | 0            | 300000   | 0               | NON_IMPOSABLE_EXONERATION_DUREE        |
      | 9  | 32500          | 32500          | 6175        | 5590                | 0             | 11765        | 148235   | 36              | IMPOSABLE_IR_PS                         |
      | 10 | 50000          | 41000          | 7790        | 8174                | 0             | 15964        | 156536   | 32              | IMPOSABLE_IR_PS                         |
      | 11 | 50000          | 35000          | 6650        | 7891                | 0             | 14541        | 150959   | 29              | IMPOSABLE_IR_PS                         |
      | 12 | 127500         | 5100           | 969         | 16140               | 0             | 17109        | 232891   | 13              | IMPOSABLE_IR_PS                         |
      | 13 | 127500         | 0              | 0           | 15790               | 0             | 15790        | 234210   | 12              | IMPOSABLE_IR_PS                  |

    When je lance la simulation PVI pour tous les scénarios
    Then les résultats PVI doivent correspondre aux attentes
