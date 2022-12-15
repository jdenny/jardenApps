package jarden.engspa;

import java.util.HashMap;

public class VerbUtils {
	public enum Tense {
		present(true),
		future(true),
		preterite(true),
		imperfect(true),
		imperative(false),
		noImperative(false);

		// if false, the conjugation is the same for all persons;
		// used when showing all conjugations of a verb.
		final boolean diffPersons;
		
		Tense(boolean diffPersons) {
			this.diffPersons = diffPersons;
		}
		public boolean isDiffPersons() {
			return diffPersons;
		}
	}
	public enum Person {
		yo(0, "yo", "I"),
		tu(1, "tú", "you"),
        el(2, "él", "he"),
		ella(2, "ella", "she"),
		usted(2, "usted", "you"),
		nosotros(3, "nosotros", "we"),
		ellos(4, "ellos", "they"),
		ellas(4, "ellas", "they"),
		ustedes(4, "ustedes", "you");

		private final static String[] spaCodes = {
			"yo", "tu", "el", "n", "e"
		};
		/*
		 * Used to index Spanish verb endings; do not tamper with it!
		 */
		private final int index;
		private final String spaPronoun;
		private final String engPronoun;
		
		Person(int index, String spaPronoun, String engPronoun) {
			this.index = index;
			this.spaPronoun = spaPronoun;
			this.engPronoun = engPronoun;
		}
		/**
		 * From the point of view of English verbs
		 * @return true if "she" or "he"
		 */
		public boolean isThirdPersonSingular() {
			return this == el || this == ella;
		}
		public String getSpaCode() {
			return spaCodes[index];
		}
		public int getIndex() {
			return this.index;
		}
		public String getSpaPronoun() {
			return this.spaPronoun;
		}
		public String getEngPronoun() {
			return this.engPronoun;
		}
	}

	static class RegularSpaVerb {
		private final String[] presentEndings;
		private final String[] preteriteEndings;
		private final String[] imperfectEndings;
		private final String siEnding;
		private final String noEnding;
		
		public RegularSpaVerb(String[] presentEndings, String[] preteriteEndings,
				String[] imperfectEndings, String siEnding, String noEnding) {
			this.presentEndings = presentEndings;
			this.preteriteEndings = preteriteEndings;
			this.imperfectEndings = imperfectEndings;
			this.siEnding = siEnding;
			this.noEnding = noEnding;
		}
		public String conjugateVerb(String verbSpa, Tense tense, Person person) {
			char cha = 'á';
			char ch2 = 'á';

			if (tense == Tense.future) {
				// future is a special case because the endings are the same for
				// all verbs; the only irregularity for some verbs is the stem.
				return verbSpa + futureEndings[person.index];
			}
			String ending = null;
			String stem = verbSpa.substring(0, verbSpa.length() - 2);
			if (tense == Tense.present) {
				ending = presentEndings[person.index];
			} else if (tense == Tense.preterite) {
				ending = preteriteEndings[person.index];
			} else if (tense == Tense.imperfect) {
				ending = imperfectEndings[person.index];
			} else if (tense == Tense.imperative) {
				ending = siEnding;
			} else if (tense == Tense.noImperative) {
				ending = noEnding;
			}
			return stem + ending;
		}
 	}

	static class IrregularSpaVerb {
		String name;
		// if any of the following are null, then there are no
		// irregularities for that tense, 
		HashMap<String, String> presentMap;
		HashMap<String, String> preteriteMap;
		HashMap<String, String> imperfectMap;
		String futureStem;
		String imperative;
		String noImperative;
		
		public IrregularSpaVerb(String name, Object[][] verbTenses) {
			this.name = name;
			for (Object[] verbTense: verbTenses) {
				String tenseCode = (String) verbTense[0];
				if (tenseCode.equals("f")) {
					futureStem = (String) verbTense[1];
				} else if (tenseCode.equals("p")) {
					presentMap = getTenseMap((Object[][]) verbTense[1]);
				} else if (tenseCode.equals("r")) {
					preteriteMap = getTenseMap((Object[][]) verbTense[1]);
				} else if (tenseCode.equals("i")) {
					imperfectMap = getTenseMap((Object[][]) verbTense[1]);
				} else if (tenseCode.equals("si")) {
					imperative = (String) verbTense[1];
				} else if (tenseCode.equals("no")) {
					noImperative = (String) verbTense[1];
				}
			}
		}
		static HashMap<String, String> getTenseMap(Object[][] tense) {
			HashMap<String, String> tenseMap = new HashMap<String, String>();
			for (Object[] person: tense) {
				String personCode = (String) person[0];
				String verbForm = (String) person[1];
				tenseMap.put(personCode, verbForm);
			}
			return tenseMap;
		}
		public String conjugate(Tense tense, Person person) {
			String conjugatedVerb = null;
			if (tense == Tense.future) {
				if (futureStem != null) {
					conjugatedVerb = futureStem + futureEndings[person.index];
				}
			} else if (tense == Tense.present) {
				if (presentMap != null) {
					//! conjugatedVerb = presentMap.get(person.name());
					conjugatedVerb = presentMap.get(person.getSpaCode());
				}
			} else if (tense == Tense.preterite) {
				if (preteriteMap != null) { 
					conjugatedVerb = preteriteMap.get(person.getSpaCode());
				}
			} else if (tense == Tense.imperfect) {
				if (imperfectMap != null) {
					conjugatedVerb = imperfectMap.get(person.getSpaCode());
				}
			} else if (tense == Tense.imperative) {
				conjugatedVerb = imperative;
			} else if (tense == Tense.noImperative) {
				conjugatedVerb = noImperative;
			}
			return conjugatedVerb;
		}
	}
	
    /**
    Returns form of regular or irregular spanish verb, based on tense and person.
    tense can be p(present), r(preterite), f(future), i(imperfect);
    not done yet: s(present subjunctive), c(conditional)
    person can be yo, tu, el, ella, n(nosotros), e(ellos), ellas - ella and ellas not yet included!
    future is different, in that the only irregularity is with the stem;
    the endings are always the same.
     * 
     */
	public static String conjugateSpanishVerb(String spanishVerb, Tense tense,
			Person person) {
		IrregularSpaVerb irregSpaVerb =
				irregSpaVerbsMap2.get(spanishVerb);
		String conjugatedVerb = null;
		if (irregSpaVerb != null) {
			conjugatedVerb = irregSpaVerb.conjugate(tense, person);
		}
		if (conjugatedVerb == null) {
			String ending = spanishVerb.substring(spanishVerb.length() - 2);
			RegularSpaVerb regularVerb;
			if (ending.equals("er")) {
				regularVerb = regularERVerb;
			} else if (ending.equals("ir") || ending.equals("ír")) {
				regularVerb = regularIRVerb;
			} else if (ending.equals("ar")) {
				regularVerb = regularARVerb;
			} else {
				throw new IllegalStateException("unrecognised verb ending:  " + ending);
			}
			conjugatedVerb = regularVerb.conjugateVerb(spanishVerb, tense, person);
		}
		return conjugatedVerb;
	}
	
	/**
		for english verb: person [prefix] verb[suffix]
	    present
	        I/you[all]/we/they cook
	        he/she/it cooks
	    imperfect
	        I/he/she/it was cooking
	        we/they/you/YOU/thou/THOU were cooking
	    future
	        I/you/he/she/it/we/they will cook
	    preterite
	        I/you/he/she/it/we/they cooked
	    
	    only suffix used so far is 'ing' for imperfect and 'ed' for preterite
	    there are many irregularities which we haven't yet coded for!
	    but the rules can be found at:
	    http://www.oxforddictionaries.com/words/verb-tenses-adding-ed-and-ing
	 */
	public static String conjugateEnglishVerb(String englishVerb, Tense tense,
			Person person) {
	    String prefix, suffix;
	    String engVerbModified = englishVerb;
	    if (tense != Tense.imperative && tense != Tense.noImperative) {
		    boolean thirdPersonSingular = person.isThirdPersonSingular();
			if (tense == Tense.present) {
				if (thirdPersonSingular) {
					String irreg = irregEnglish3rdPersonSingularPresentMap.get(englishVerb);
			        if (irreg != null) {
			        	engVerbModified = irreg;
			        } else if (englishVerb.endsWith("ss") ||
                            englishVerb.endsWith("sh") ||
                            englishVerb.endsWith("ch")) {
			        	engVerbModified = englishVerb + "es";
			        } else {
			        	engVerbModified = englishVerb + "s";
			        }
				} else if (englishVerb.equals("be")) {
		        	if (person == Person.yo) engVerbModified = "am";
		        	else engVerbModified = "are";
			    }
		    } else if (tense == Tense.imperfect) {
                if (englishVerb.equals("can")) engVerbModified = "could";
                else {
                    if (thirdPersonSingular || person == Person.yo) prefix = "was";
                    else prefix = "were";
                    suffix = "ing";
                    String stem = irregEnglishGerundMap.get(englishVerb);
                    if (stem == null) {
                        if (englishVerb.endsWith("e") && !englishVerb.equals("be") &&
                                !englishVerb.equals("see")) {
                            stem = englishVerb.substring(0, englishVerb.length() - 1);
                        } else if (englishVerb.endsWith("et") && !englishVerb.endsWith("eet")) {
                            stem = englishVerb + "t";
                        } else {
                            stem = englishVerb;
                        }
                    }
                    engVerbModified = prefix + " " + stem + suffix;
                }
		    } else if (tense == Tense.future) {
                if (englishVerb.equals("can")) {
                    engVerbModified = "will be able";
                } else {
                    engVerbModified = "will " + englishVerb;
                }
		    } else if (tense == Tense.preterite) {
		        if (englishVerb.equals("be")) {
		        	if (thirdPersonSingular || person == Person.yo) engVerbModified = "was";
		        	else engVerbModified = "were";
		        } else {
		            String irregEnglishPreterite = irregEnglishPreteritesMap.get(englishVerb);
		            if (irregEnglishPreterite == null) {
		                if (englishVerb.endsWith("e")) {
		                    engVerbModified = englishVerb + "d";
		                } else {
		                    engVerbModified = englishVerb + "ed";
		                }
		            } else {
		                engVerbModified = irregEnglishPreterite;
		            }
		        }
		    } else {
		    	throw new IllegalStateException("unrecognised tense: " + tense);
		    }
	    }
	    return engVerbModified;
	}
	
	private static final String[] futureEndings = {
		"é", "ás", "á", "emos", "án"
	};
	private static final String[] erPresentEndings = {
		"o", "es", "e", "emos", "en"
	};
	private static final String[] erPreteriteEndings = {
		"í", "iste", "ió", "imos", "ieron"
	};
	private static final String[] erImperfectEndings = {
		"ía", "ías", "ía", "íamos", "ían"
	};
	
	private static final String[] arPresentEndings = {
		"o", "as", "a", "amos", "an"
	};
	private static final String[] arPreteriteEndings = {
		"é", "aste", "ó", "amos", "aron"
	};
	private static final String[] arImperfectEndings = {
		"aba", "abas", "aba", "ábamos", "aban"
	};
	private static final String[] irPresentEndings = {
		"o", "es", "e", "imos", "en"
	};
	
	private static final String[][] agradecerPresent = { {"yo", "agradezco"} };
	private static final Object[][] agradecerTenses = {
		{"p", agradecerPresent}, {"no", "agradezcas"} 
	};

	private static final String[][] alcanzarPreterite = { {"yo", "alcancé"} };
	private static final Object[][] alcanzarTenses = { {"r", alcanzarPreterite} };

	private static final String[][] aparcarPreterite = { {"yo", "aparqué"} };
	private static final Object[][] aparcarTenses = {
		{"r", aparcarPreterite}, {"no", "aparques"}
	};
	
	private static final String[][] caerPresent = {
		{"yo", "caigo"}
	};
	private static final String[][] caerPreterite = {
		{"tu", "caíste"}, {"el", "cayó"},
		{"n", "caímos"}, {"e", "cayeron"}
	};
	private static final Object[][] caerTenses = {
		{"p", caerPresent}, {"r", caerPreterite},
		{"si", "cae"}, {"no", "caigas"}
	};
	
	private static final String[][] cargarPreterite = { {"yo", "cargué"} };
	private static final Object[][] cargarTenses = {
		{"r", cargarPreterite}, {"no", "cargues"}
	};
	
	private static final String[][] chocarPreterite = { {"yo", "choqué"} };
	private static final Object[][] chocarTenses = {
		{"r", chocarPreterite}, {"no", "choques"}
	};
	
	private static final String[][] conocerPresent = {
		{"yo", "conozco"}
	};
	private static final Object[][] conocerTenses = {
		{"p", conocerPresent}, {"no", "conozcas"}
	};

	private static final String[][] contarPresent = {
		{"yo", "cuento"}, {"tu", "cuentas"}, {"el", "cuenta"},
		{"e", "cuentan"}
	};
	private static final Object[][] contarTenses = {
		{"p", contarPresent}, {"si", "cuenta"}, {"no", "cuentes"}
	};

	private static final String[][] continuarPresent = {
		{"yo", "continúo"}, {"tu", "continúas"}, {"el", "continúa"},
		{"e", "continúan"}
	};
	private static final Object[][] continuarTenses = {
		{"p", continuarPresent}, {"si", "continúa"}, {"no", "continúes"}

	};

	private static final String[][] costarPresent = {
		{"yo", "cuesto"}, {"tu", "cuestas"}, {"el", "cuesta"},
		{"e", "cuestan"}
	};
	private static final Object[][] costarTenses = {
		{"p", costarPresent}, {"si", "cuesta"}, {"no", "cuestes"}
	};

	private static final String[][] creerPreterite = {
		{"tu", "creíste"}, {"el", "creyó"}, {"n", "creímos"},
		{"e", "creyeron"}
	};
	private static final Object[][] creerTenses = {
		{"r", creerPreterite}
	};

	private static final String[][] cruzarPreterite = { {"yo", "crucé"} };
	private static final Object[][] cruzarTenses = {
		{"r", cruzarPreterite}, {"no", "cruces"}
	};
	
	private static final String[][] darPresent = { {"yo", "doy"} };
	private static final String[][] darPreterite = {
		{"yo", "di"}, {"tu", "diste"}, {"el", "dio"}, {"n", "dimos"},
		{"e", "dieron"}
	};
	private static final Object[][] darTenses = {
		{"p", darPresent}, {"r", darPreterite}
	};
	
	private static final String[][] decirPresent = {
		{"yo", "digo"}, {"tu", "dices"}, {"el", "dice"},
		{"e", "dicen"}
	};
	private static final String[][] decirPreterite = {
		{"yo", "dije"}, {"tu", "dijiste"}, {"el", "dijo"},
		{"n", "dijimos"}, {"e", "dijeron"}
	};
	private static final Object[][] decirTenses = {
		{"p", decirPresent}, {"r", decirPreterite},
		{"f", "dir"}, {"si", "di"}, {"no", "digas"}
	};
	
	private static final String[][] descargarPreterite = { {"yo", "descargué"} };
	private static final Object[][] descargarTenses = {
		{"r", descargarPreterite}, {"no", "descargues"}
	};

	private static final String[][] despedirPresent = {
		{"yo", "despido"}, {"tu", "despides"}, {"el", "despide"},
		{"e", "despiden"}
	};
	private static final String[][] despedirPreterite = {
		{"el", "despidió"}
	};
	private static final Object[][] despedirTenses = {
		{"p", despedirPresent}, {"r", despedirPreterite},
		{"si", "despide"}, {"no", "despidas"}
	};
	
	private static final String[][] despertarPresent = {
		{"yo", "despierto"}, {"tu", "despiertas"}, {"el", "despierta"},
		{"e", "despiertan"}
	};
	private static final Object[][] despertarTenses = {
		{"p", despertarPresent}, {"si", "despierta"}, {"no", "despiertes"}
	};
	
	private static final String[][] dirigirPresent = { {"yo", "dirijo"} };
	private static final Object[][] dirigirTenses = {
		{"p", dirigirPresent}, {"no", "dirijas"}
	};
	
	private static final String[][] dolerPresent = {
		{"yo", "duelo"}, {"tu", "dueles"},
		{"el", "duele"}, {"e", "duelen"}
	};
	private static final Object[][] dolerTenses = {
		{"p", dolerPresent}, {"si", "duele"}, {"no", "duelas"}
	};

	private static final String[][] dormirPresent = {
		{"yo", "duermo"}, {"tu", "duermes"}, {"el", "duerme"},
		{"e", "duermen"}
	};
	private static final String[][] dormirPreterite = {
		{"el", "durmió"}, {"e", "durmieron"}
	};
	private static final Object[][] dormirTenses = {
		{"p", dormirPresent}, {"r", dormirPreterite},
		{"si", "duerme"}, {"no", "duermas"}
	};
	
	private static final String[][] encontrarPresent = {
		{"yo", "encuentro"}, {"tu", "encuentras"}, {"el", "encuentra"},
		{"e", "encuentran"}
	};
	private static final Object[][] encontrarTenses = {
		{"p", encontrarPresent},
		{"si", "encuentra"}, {"no", "encuentres"}
	};
	private static final String[][] ejercerPresent = {
		{"yo", "ejerzo"}
	};
	private static final Object[][] ejercerTenses = {
		{"p", ejercerPresent}, {"no", "ejerzas"}
	};

	private static final String[][] entenderPresent = {
		{"yo", "entiendo"}, {"tu", "entiendes"},
		{"el", "entiende"}, {"e", "entienden"}
	};
	private static final Object[][] entenderTenses = {
		{"p", entenderPresent}, {"si", "entiende"}, {"no", "entiendas"}
	};

	private static final String[][] enviarPresent = {
		{"yo", "envío"}, {"tu", "envías"},
		{"el", "envía"}, {"e", "envían"}
	};
	private static final Object[][] enviarTenses = {
		{"p", enviarPresent}, {"si", "envía"}, {"no", "envíes"}
	};

    private static final String[][] escogerPresent = {
            {"yo", "escojo"}
    };
    private static final Object[][] escogerTenses = {
            {"p", escogerPresent}, {"no", "escojas"}
    };

	private static final String[][] estarPresent = {
		{"yo", "estoy"}, {"tu", "estás"}, {"el", "está"},
		{"n", "estamos"}, {"e", "están"}
	};
	private static final String[][] estarPreterite = {
		{"yo", "estuve"}, {"tu", "estuviste"}, {"el", "estuvo"},
		{"n", "estuvimos"}, {"e", "estuvieron"}
	};
	private static final Object[][] estarTenses = {
		{"p", estarPresent}, {"r", estarPreterite},
		{"si", "está"}, {"no", "estés"}
	};
	
	private static final String[][] haberPresent = {
		{"yo", "he"}, {"tu", "has"}, {"el", "ha"},
		{"n", "hemos"}, {"e", "han"}
	};
	private static final String[][] haberPreterite = {
		{"yo", "hube"}, {"tu", "hubiste"}, {"el", "hubo"},
		{"n", "hubimos"}, {"e", "hubieron"}
	};
	private static final Object[][] haberTenses = {
		{"p", haberPresent}, {"r", haberPreterite},
		{"f", "habr"}, {"no", "hayas"}
	};
	
	private static final String[][] hacerPresent = { {"yo", "hago"} };
	private static final String[][] hacerPreterite = {
		{"yo", "hice"}, {"tu", "hiciste"}, {"el", "hizo"},
		{"n", "hicimos"}, {"e", "hicieron"}
	};
	private static final Object[][] hacerTenses = {
		{"p", hacerPresent}, {"r", hacerPreterite},
		{"f", "har"}, {"si", "haz"}, {"no", "hagas"}
	};

	private static final String[][] helarPresent = {
		{"yo", "hielo"}, {"tu", "hielas"},
		{"el", "hiela"}, {"e", "hielan"}
	};
	private static final Object[][] helarTenses = {
		{"p", helarPresent}, {"si", "hiela"}, {"no", "hieles"}
	};

	private static final String[][] irPresent = {
		{"yo", "voy"}, {"tu", "vas"}, {"el", "va"},
		{"n", "vamos"}, {"e", "van"}
	};
	private static final String[][] irPreterite = {
		{"yo", "fui"}, {"tu", "fuiste"}, {"el", "fue"},
		{"n", "fuimos"}, {"e", "fueron"}
	};
	private static final String[][] irImperfect = {
		{"yo", "iba"}, {"tu", "ibas"}, {"el", "iba"},
		{"n", "íbamos"},  {"e", "iban"}
	};
	private static final Object[][] irTenses = {
		{"p", irPresent}, {"r", irPreterite}, {"i", irImperfect},
		{"si", "ve"}, {"no", "vayas"}
	};
	
	private static final String[][] jugarPresent = {
		{"yo", "juego"}, {"tu", "juegas"}, {"el", "juega"},
		{"n", "jugamos"}, {"e", "juegan"}
	};
	private static final String[][] jugarPreterite = { {"yo", "jugué"} };
	private static final Object[][] jugarTenses = {
		{"p", jugarPresent}, {"r", jugarPreterite},
		{"si", "juega"}, {"no", "juegues"}
	};
	
	private static final String[][] leerPreterite = {
		{"tu", "leíste"}, {"el", "leyó"}, {"n", "leímos"},
		{"e", "leyeron"}
	};
	private static final Object[][] leerTenses = {
		{"r", leerPreterite}
	};
	
	private static final String[][] llegarPreterite = { {"yo", "llegué"} };
	private static final Object[][] llegarTenses = {
		{"r", llegarPreterite}, {"no", "llegues"}
	};
	
	private static final String[][] lloverPresent = {
		{"yo", "lluevo"}, {"tu", "llueves"},
		{"el", "llueve"}, {"e", "llueven"}
	};
	private static final Object[][] lloverTenses = {
		{"p", lloverPresent}, {"si", "llueve"}, {"no", "lluevas"}
	};
	
	private static final String[][] merecerPresent = { {"yo", "merezco"} };
	private static final Object[][] merecerTenses = {
		{"p", merecerPresent}, {"no", "merezcas"}
	};
	
	private static final String[][] morirPresent = {
		{"yo", "muero"}, {"tu", "mueres"}, {"el", "muere"},
		{"e", "mueren"}
	};
	private static final String[][] morirPreterite = {
		{"el", "murió"}, {"e", "murieron"}
	};
	private static final Object[][] morirTenses = {
		{"p", morirPresent}, {"r", morirPreterite},
		{"si", "muere"}, {"no", "mueras"}
	};

	private static final String[][] mostrarPresent = {
		{"yo", "muestro"}, {"tu", "muestras"},
		{"el", "muestra"}, {"e", "muestran"}
	};
	private static final Object[][] mostrarTenses = {
		{"p", mostrarPresent}, {"si", "muestra"}, {"no", "muestres"}
	};

	private static final String[][] nevarPresent = {
		{"yo", "nievo"}, {"tu", "nievas"}, {"el", "nieva"},
		{"e", "nievan"}
	};
	private static final Object[][] nevarTenses = {
		{"p", nevarPresent}, {"si", "nieva"}, {"no", "nieves"}
	};

	private static final String[][] oirPresent = {
		{"yo", "oigo"}, {"tu", "oyes"}, {"el", "oye"}, {"n", "oímos"},
		{"e", "oyen"}
	};
	private static final String[][] oirPreterite = {
		{"tu", "oíste"}, {"el", "oyó"}, {"n", "oímos"}, {"e", "oyeron"}
	};
	private static final Object[][] oirTenses = {
		{"p", oirPresent}, {"r", oirPreterite}, {"f", "oir"},
		{"si", "oye"}, {"no", "oigas"}
	};

	private static final String[][] parecerPresent = { {"yo", "parezco"} };
	private static final Object[][] parecerTenses = {
		{"p", parecerPresent}, {"no", "parezcas"}
	};

	private static final String[][] padecerPresent = { {"yo", "padezco"} };
	private static final Object[][] padecerTenses = {
		{"p", padecerPresent}, {"no", "padezcas"}
	};

	private static final String[][] pedirPresent = {
		{"yo", "pido"}, {"tu", "pides"}, {"el", "pide"}, {"e", "piden"}
	};
	private static final String[][] pedirPreterite = {
		{"el", "pidió"}, {"e", "pidieron"}
	};
	private static final Object[][] pedirTenses = {
		{"p", pedirPresent}, {"r", pedirPreterite},
		{"si", "pide"}, {"no", "pidas"}
	};

	private static final String[][] pegarPreterite = { {"yo", "pegué"} };
	private static final Object[][] pegarTenses = {
		{"r", pegarPreterite}, {"no", "pegues"}
	};
	
	private static final String[][] pensarPresent = {
		{"yo", "pienso"}, {"tu", "piensas"}, {"el", "piensa"}, {"e", "piensan"}
	};
	private static final Object[][] pensarTenses = {
		{"p", pensarPresent}, {"si", "piensa"}, {"no", "pienses"}
	};

	private static final String[][] perderPresent = {
		{"yo", "pierdo"}, {"tu", "pierdes"}, {"el", "pierde"}, {"e", "pierden"}
	};
	private static final Object[][] perderTenses = {
		{"p", perderPresent},
		{"si", "pierde"}, {"no", "pierdas"}
	};

	private static final String[][] pescarPreterite = {
		{"yo", "pesqué"}
	};
	private static final Object[][] pescarTenses = {
		{"r", pescarPreterite}, {"no", "pesques"}
	};

	private static final String[][] poderPresent = {
		{"yo", "puedo"}, {"tu", "puedes"}, {"el", "puede"}, {"e", "pueden"}
	};
	private static final String[][] poderPreterite = {
		{"yo", "pude"}, {"tu", "pudiste"}, {"el", "pudo"},
		{"n", "pudimos"}, {"e", "pudieron"}
	};
	private static final Object[][] poderTenses = {
		{"p", poderPresent}, {"r", poderPreterite},
		{"f", "podr"}, {"si", "puede"}, {"no", "puedas"}
	};

	private static final String[][] ponerPresent = { {"yo", "pongo"} };
	private static final String[][] ponerPreterite = {
		{"yo", "puse"}, {"tu", "pusiste"}, {"el", "puso"},
		{"n", "pusimos"}, {"e", "pusieron"}
	};
	private static final Object[][] ponerTenses = {
		{"p", ponerPresent}, {"r", ponerPreterite},
		{"f", "pondr"}, {"si", "pon"}, {"no", "pongas"}
	};

	private static final String[][] preferirPresent = {
		{"yo", "prefiero"}, {"tu", "prefieres"}, {"el", "prefiere"},
		{"e", "prefieren"}
	};
	private static final String[][] preferirPreterite = {
		{"el", "prefirió"}, {"e", "prefirieron"}
	};
	private static final Object[][] preferirTenses = {
		{"p", preferirPresent}, {"r", preferirPreterite},
		{"si", "prefiere"}, {"no", "prefieras"}
	};

	private static final String[][] prevenirPresent = {
		{"yo", "prevengo"}, {"tu", "previenes"}, {"el", "previene"},
		{"e", "previenen"}
	};
	private static final String[][] prevenirPreterite = {
		{"yo", "previne"}, {"tu", "previniste"}, {"el", "previno"},
		{"n", "previnimos"}, {"e", "previnieron"}
	};
	private static final Object[][] prevenirTenses = {
		{"p", prevenirPresent}, {"r", prevenirPreterite}, {"f", "prevendr"},
		{"si", "prevén"}, {"no", "prevengas"}
	};

	private static final String[][] quebrarPresent = {
		{"yo", "quiebro"}, {"tu", "quiebras"}, {"el", "quiebra"},
		{"e", "quiebran"}
		};
	private static final Object[][] quebrarTenses = {
		{"p", quebrarPresent}, {"si", "quiebra"}, {"no", "quiebres"}
	};

	private static final String[][] quererPresent = {
		{"yo", "quiero"}, {"tu", "quieres"},
		{"el", "quiere"}, {"e", "quieren"}
	};
	private static final String[][] quererPreterite = {
		{"yo", "quise"}, {"tu", "quisiste"}, {"el", "quiso"},
		{"n", "quisimos"}, {"e", "quisieron"}
	};
	private static final Object[][] quererTenses = {
		{"p", quererPresent}, {"r", quererPreterite},
		{"f", "querr"}, {"si", "quiere"}, {"no", "quieras"}
	};

	private static final String[][] recordarPresent = {
		{"yo", "recuerdo"}, {"tu", "recuerdas"}, {"el", "recuerda"},
		{"e", "recuerdan"}
	};
	private static final Object[][] recordarTenses = {
		{"p", recordarPresent}, {"si", "recuerda"}, {"no", "recuerdes"}
	};

	private static final String[][] reirPresent = {
		{"yo", "río"}, {"tu", "ríes"}, {"el", "ríe"}, {"n", "reímos"},
		{"e", "ríen"}
	};
	private static final String[][] reirPreterite = {
		{"tu", "reíste"}, {"el", "rió"}, {"n", "reímos"}, {"e", "rieron"}

	};
	private static final Object[][] reirTenses = {
		{"p", reirPresent}, {"r", reirPreterite},
		{"f", "reir"}, {"si", "ríe"}, {"no", "rías"}
	};

	private static final String[][] renirPresent = {
		{"yo", "riño"}, {"tu", "riñes"}, {"el", "riñe"},
		{"e", "riñen"}
	};
	private static final String[][] renirPreterite = {
		{"el", "riñó"}, {"e", "riñeron"}

	};
	private static final Object[][] renirTenses = {
		{"p", renirPresent}, {"r", renirPreterite},
		{"si", "riñe"}, {"no", "riñas"}
	};

	private static final String[][] saberPresent = {
		{"yo", "sé"}
	};
	private static final String[][] saberPreterite = {
		{"yo", "supe"}, {"tu", "supiste"}, {"el", "supo"},
		{"n", "supimos"}, {"e", "supieron"}
	};
	private static final Object[][] saberTenses = {
		{"p", saberPresent}, {"r", saberPreterite},
		{"f", "sabr"}, {"no", "sepas"}
	};

	private static final String[][] salirPresent = { {"yo", "salgo"} };
	private static final Object[][] salirTenses = {
		{"p", salirPresent}, {"f", "saldr"}, {"si", "sal"}, {"no", "salgas"}
	};

	private static final String[][] seguirPresent = {
		{"yo", "sigo"}, {"tu", "sigues"}, {"el", "sigue"},
		{"n", "seguimos"}, {"e", "siguen"}
	};
	private static final String[][] seguirPreterite = {
		{"el", "siguió"}, {"e", "siguieron"}
	};
	private static final Object[][] seguirTenses = {
		{"p", seguirPresent}, {"r", seguirPreterite},
		{"si", "sigue"}, {"no", "sigas"}
	};

	private static final String[][] sentarPresent = {
		{"yo", "siento"}, {"tu", "sientas"},
		{"el", "sienta"}, {"e", "sientan"}
	};
	private static final Object[][] sentarTenses = {
		{"p", sentarPresent}, {"si", "sienta"}, {"no", "sientes"}
	};

	private static final String[][] serPresent = {
		{"yo", "soy"}, {"tu", "eres"}, {"el", "es"},
		{"n", "somos"}, {"e", "son"}
	};
	private static final String[][] serPreterite = {
		{"yo", "fui"}, {"tu", "fuiste"}, {"el", "fue"},
		{"n", "fuimos"}, {"e", "fueron"}
	};
	private static final String[][] serImperfect = {
		{"yo", "era"}, {"tu", "eras"}, {"el", "era"},
		{"n", "éramos"}, {"e", "eran"}
	};
	private static final Object[][] serTenses = {
		{"p", serPresent}, {"r", serPreterite},
		{"i", serImperfect}, {"si", "sé"}, {"no", "seas"}
	};

	private static final String[][] servirPresent = {
		{"yo", "sirvo"}, {"tu", "sirves"}, {"el", "sirve"}, {"e", "sirven"}
	};
	private static final String[][] servirPreterite = {
		{"el", "sirvió"}, {"e", "sirvieron"}
	};
	private static final Object[][] servirTenses = {
		{"p", servirPresent}, {"r", servirPreterite},
		{"si", "sirve"}, {"no", "sirvas"}
	};
	
	private static final String[][] sonarPresent = {
		{"yo", "sueno"}, {"tu", "suenas"},
		{"el", "suena"}, {"e", "suenan"}
	};
	private static final Object[][] sonarTenses = {
		{"p", sonarPresent}, {"si", "suena"}, {"no", "suenes"}
	};

	private static final String[][] sonreirPresent = {
		{"yo", "sonrío"}, {"tu", "sonríes"}, {"el", "sonríe"},
		{"n", "sonreímos"}, {"e", "sonríen"}
	};
	private static final String[][] sonreirPreterite = {
		{"tu", "sonreíste"}, {"el", "sonrió"}, {"n", "sonreímos"}, {"e", "sonrieron"}
	};
	private static final Object[][] sonreirTenses = {
		{"p", sonreirPresent}, {"r", sonreirPreterite},
		{"f", "sonreir"}, {"si", "sonríe"}, {"no", "sonrías"}
	};

	private static final String[][] tenerPresent = {
		{"yo", "tengo"}, {"tu", "tienes"}, {"el", "tiene"}, {"e", "tienen"}
	};
	private static final String[][] tenerPreterite = {
		{"yo", "tuve"}, {"tu", "tuviste"}, {"el", "tuvo"},
		{"n", "tuvimos"}, {"e", "tuvieron"}
	};
	private static final Object[][] tenerTenses = {
		{"p", tenerPresent}, {"r", tenerPreterite},
		{"f", "tendr"}, {"si", "ten"}, {"no", "tengas"}
	};

	private static final String[][] tocarPreterite = { {"yo", "toqué"} };
	private static final Object[][] tocarTenses = {
		{"r", tocarPreterite}, {"no", "toques"}
	};

	private static final String[][] torcerPresent = {
		{"yo", "tuerzo"}, {"tu", "tuerces"}, {"el", "tuerce"}, {"e", "tuercen"}
	};
	private static final Object[][] torcerTenses = {
		{"p", torcerPresent}, {"si", "tuerce"}, {"no", "tuerzas"}
	};

	private static final String[][] traerPresent = { {"yo", "traigo"} };
	private static final String[][] traerPreterite = {
		{"yo", "traje"}, {"tu", "trajiste"}, {"el", "trajo"},
		{"n", "trajimos"}, {"e", "trajeron"}
	};
	private static final Object[][] traerTenses = {
		{"p", traerPresent}, {"r", traerPreterite}, {"no", "traigas"}
	};

	private static final String[][] valerPresent = { {"yo", "valgo"} };
	private static final Object[][] valerTenses = {
		{"p", valerPresent}, {"f", "valdr"}, {"si", "val"},
		{"no", "valgas"}
	};

	private static final String[][] venirPresent = {
		{"yo", "vengo"}, {"tu", "vienes"}, {"el", "viene"},
		{"e", "vienen"}
	};
	private static final String[][] venirPreterite = {
		{"yo", "vine"}, {"tu", "viniste"}, {"el", "vino"},
		{"n", "vinimos"}, {"e", "vinieron"}
	};
	private static final Object[][] venirTenses = {
		{"p", venirPresent}, {"r", venirPreterite}, {"f", "vendr"},
		{"si", "ven"}, {"no", "vengas"}
	};

	private static final String[][] verPresent = { {"yo", "veo"} };
	private static final String[][] verPreterite = {
		{"yo", "vi"}, {"el", "vio"}
	};
	private static final String[][] verImperfect = {
		{"yo", "veía"}, {"tu", "veías"}, {"el", "veía"},
		{"n", "veíamos"}, {"e", "veían"}
	};
	private static final Object[][] verTenses = {
		{"p", verPresent}, {"r", verPreterite},
		{"i", verImperfect}, {"no", "veas"}
	};

	private static final Object[][] irregSpaVerbs = {
		{"agradecer", agradecerTenses}, {"alcanzar", alcanzarTenses},
		{"aparcar", aparcarTenses},
		{"caer", caerTenses}, {"cargar", cargarTenses},
		{"chocar", chocarTenses}, {"conocer", conocerTenses},
		{"contar", contarTenses}, {"continuar", continuarTenses},
		{"costar", costarTenses},
		{"creer", creerTenses}, {"curzar", cruzarTenses}, {"dar", darTenses},
		{"decir", decirTenses}, {"despertar", despertarTenses},
		{"despedir", despedirTenses}, {"dirigir", dirigirTenses},
		{"doler", dolerTenses}, {"dormir", dormirTenses},
		{"descargar", descargarTenses},{"ejercer", ejercerTenses},
		{"encontrar", encontrarTenses}, {"estar", estarTenses},
        {"escoger", escogerTenses},
		{"helar", entenderTenses}, {"enviar", enviarTenses},
		{"haber", haberTenses}, {"hacer", hacerTenses},
		{"helar", helarTenses}, {"ir", irTenses},
		{"jugar", jugarTenses}, {"leer", leerTenses},
		{"llegar", llegarTenses}, {"llover", lloverTenses},
		{"merecer", merecerTenses}, {"morir", morirTenses},
		{"mostrar", mostrarTenses}, {"nevar", nevarTenses},
		{"oír", oirTenses}, {"padecer", padecerTenses},
		{"parecer", parecerTenses}, {"pedir", pedirTenses},
		{"pegar", pegarTenses},
		{"pensar", pensarTenses}, {"perder", perderTenses},
		{"pescar", pescarTenses},
		{"poder", poderTenses}, {"poner", ponerTenses},
		{"preferir", preferirTenses},
		{"prevenir", prevenirTenses}, {"quebrar", quebrarTenses},
		{"querer", quererTenses}, {"recordar", recordarTenses},
		{"reír", reirTenses},{"reñir", renirTenses},
		{"saber", saberTenses}, {"salir", salirTenses},
		{"seguir", seguirTenses}, {"sentar", sentarTenses},
		{"ser", serTenses}, {"servir", servirTenses},
		{"sonar", sonarTenses},
		{"sonreír", sonreirTenses}, {"tener", tenerTenses},
		{"torcer", torcerTenses},
		{"tocar", tocarTenses}, {"traer", traerTenses},
		{"valer", valerTenses}, {"venir", venirTenses},
		{"ver", verTenses}
	};
	private static final HashMap<String, IrregularSpaVerb> irregSpaVerbsMap2;
	private static final RegularSpaVerb regularERVerb;
	private static final RegularSpaVerb regularIRVerb;
	private static final RegularSpaVerb regularARVerb;
	
	/******************English verb data*****************************/
	private static final String[][] irregEnglish3rdPersonSingularPresent = {
            {"accompany", "accompanies"}, {"be", "is"},
            {"can", "can"}, {"carry", "carries"},
            {"cry", "cries"},
            { "do", "does"}, { "go", "goes" },
            {"have", "has"},
            {"reply", "replies"}, {"study", "studies"},
            {"tidy", "tidies"}, {"try", "tries"},
	};
	private static final String[][] irregEnglishPreterites = {
		{"accompany", "accompanied"},
		{"awake", "awoke"}, {"bring", "brought"},
		{"break", "broke"}, {"buy", "bought"}, {"can", "could"}, 
		{"carry", "carried"}, {"chat", "chatted"}, {"choose", "chose"},
		{"come", "came"}, {"cost", "cost"}, {"cry", "cried"},
		{"do", "did"}, {"draw", "drew"}, {"drink", "drank"}, {"eat", "ate"},
		{"fall", "fell"}, {"fight", "fought"}, {"forget", "forgot"},
		{"forgive", "forgave"}, {"freeze", "froze"},
		{"give", "gave"}, {"go", "went"}, {"have", "had"},
		{"hear", "heard"}, {"hide", "hid"}, {"hit", "hit"},
		{"hurt", "hurt"}, {"hold", "held"}, {"know", "knew"},
		{"leave", "left"}, {"lend", "lent"}, {"lose", "lost"}, {"make", "made"},
		{"meet", "met"}, {"pay", "paid"}, {"prefer", "preferred"},
		{"put", "put"}, {"read", "read"}, {"regret", "regretted"},
		{"reply", "replied"}, {"ring", "rang"}, {"run", "ran"}, {"say", "said"},
		{"see", "saw"}, {"send", "sent"}, {"sell", "sold"}, {"shoot", "shot"},
		{"sing", "sang"}, {"sit", "sat"}, {"sleep", "slept"},
		{"speak", "spoke"}, {"spend", "spent"}, {"stand", "stood"},
		{"steal", "stole"}, {"stick", "stuck"}, {"stop", "stopped"}, {"study", "studied"},
		{"swim", "swam"}, {"take", "took"}, {"teach", "taught"},
		{"tell", "told"}, {"think", "thought"}, {"throw", "threw"},
		{"tidy", "tidied"}, {"try", "tried"}, {"understand", "understood"},
		{"wear", "wore"}, {"win", "won"}, {"write", "wrote"}
	};
	// irregularities when adding 'ing'
	private static final String[][] irregEnglishGerundStem = {
		{"chat", "chatt"}, {"die", "dy"}, {"forget", "forgett"},
		{"hit", "hitt"}, {"prefer", "preferr"}, {"put", "putt"},
		{"quarrel", "quarrell"}, {"run", "runn"},
		{"regret", "regrett"}, {"stop", "stopp"}, {"swim", "swimm"}, {"win", "winn"}
	};
	private static final HashMap<String, String> irregEnglish3rdPersonSingularPresentMap;
	private static final HashMap<String, String> irregEnglishPreteritesMap;
	private static final HashMap<String, String> irregEnglishGerundMap;

	static {
		regularERVerb = new RegularSpaVerb(erPresentEndings, erPreteriteEndings,
				erImperfectEndings, "e", "as");
		regularIRVerb = new RegularSpaVerb(irPresentEndings, erPreteriteEndings,
				erImperfectEndings, "e", "as");
		regularARVerb = new RegularSpaVerb(arPresentEndings, arPreteriteEndings,
				arImperfectEndings, "a", "es");

		irregSpaVerbsMap2 = new HashMap<String, IrregularSpaVerb>();
		for (Object[] irregSpaVerb: irregSpaVerbs) {
			String verbSpa = (String) irregSpaVerb[0];
			IrregularSpaVerb irregularSpaVerb =
					new IrregularSpaVerb(verbSpa, (Object[][]) irregSpaVerb[1]);
			irregSpaVerbsMap2.put(verbSpa, irregularSpaVerb);
		}
		
		irregEnglish3rdPersonSingularPresentMap = new HashMap<String, String>();
		for (String[] irreg3PSP: irregEnglish3rdPersonSingularPresent) {
			irregEnglish3rdPersonSingularPresentMap.put(irreg3PSP[0], irreg3PSP[1]);
		}

		irregEnglishPreteritesMap = new HashMap<String, String>();
		for (String[] irregPret: irregEnglishPreterites) {
			irregEnglishPreteritesMap.put(irregPret[0], irregPret[1]);
		}

		irregEnglishGerundMap = new HashMap<String, String>();
		for (String[] irregGerund: irregEnglishGerundStem) {
			irregEnglishGerundMap.put(irregGerund[0], irregGerund[1]);
		}
	}
	
}
