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
		tu(1, "tú", "thou"),
		el(2, "él", "he"),
		ella(2, "ella", "she"),
		usted(2, "usted", "you"),
		nosotros(3, "nosotros", "we"),
		ellos(4, "ellos", "they"),
		ellas(4, "ellas", "they"),
		ustedes(4, "ustedes", "YOU");
		
		private final static String[] spaCodes = {
			"yo", "tu", "el", "n", "e"
		};
		/*
		 * Used to index Spanish verb endings; do not tamper with it!
		 */
		private int index;
		private String spaPronoun;
		private String engPronoun;
		
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
		private String[] presentEndings;
		private String[] preteriteEndings;
		private String[] imperfectEndings;
		private String siEnding;
		private String noEnding;
		
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
	    if (tense == Tense.imperative || tense == Tense.noImperative) {
	    	// end of story; note that for these two tenses, person may be null
	    } else {
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
	
	private static String[] futureEndings = {
		"é", "ás", "á", "emos", "án"
	};
	private static String[] erPresentEndings = {
		"o", "es", "e", "emos", "en"
	};
	private static String[] erPreteriteEndings = {
		"í", "iste", "ió", "imos", "ieron"
	};
	private static String[] erImperfectEndings = {
		"ía", "ías", "ía", "íamos", "ían"
	};
	
	private static String[] arPresentEndings = {
		"o", "as", "a", "amos", "an"
	};
	private static String[] arPreteriteEndings = {
		"é", "aste", "ó", "amos", "aron"
	};
	private static String[] arImperfectEndings = {
		"aba", "abas", "aba", "ábamos", "aban"
	};
	private static String[] irPresentEndings = {
		"o", "es", "e", "imos", "en"
	};
	
	private static String[][] agradecerPresent = { {"yo", "agradezco"} };
	private static Object[][] agradecerTenses = {
		{"p", agradecerPresent}, {"no", "agradezcas"} 
	};

	private static String[][] alcanzarPreterite = { {"yo", "alcancé"} };
	private static Object[][] alcanzarTenses = { {"r", alcanzarPreterite} };

	private static String[][] aparcarPreterite = { {"yo", "aparqué"} };
	private static Object[][] aparcarTenses = {
		{"r", aparcarPreterite}, {"no", "aparques"}
	};
	
	private static String[][] caerPresent = {
		{"yo", "caigo"}
	};
	private static String[][] caerPreterite = {
		{"tu", "caíste"}, {"el", "cayó"},
		{"n", "caímos"}, {"e", "cayeron"}
	};
	private static Object[][] caerTenses = {
		{"p", caerPresent}, {"r", caerPreterite},
		{"si", "cae"}, {"no", "caigas"}
	};
	
	private static String[][] cargarPreterite = { {"yo", "cargué"} };
	private static Object[][] cargarTenses = {
		{"r", cargarPreterite}, {"no", "cargues"}
	};
	
	private static String[][] chocarPreterite = { {"yo", "choqué"} };
	private static Object[][] chocarTenses = {
		{"r", chocarPreterite}, {"no", "choques"}
	};
	
	private static String[][] conocerPresent = {
		{"yo", "conozco"}
	};
	private static Object[][] conocerTenses = {
		{"p", conocerPresent}, {"no", "conozcas"}
	};

	private static String[][] contarPresent = {
		{"yo", "cuento"}, {"tu", "cuentas"}, {"el", "cuenta"},
		{"e", "cuentan"}
	};
	private static Object[][] contarTenses = {
		{"p", contarPresent}, {"si", "cuenta"}, {"no", "cuentes"}
	};

	private static String[][] continuarPresent = {
		{"yo", "continúo"}, {"tu", "continúas"}, {"el", "continúa"},
		{"e", "continúan"}
	};
	private static Object[][] continuarTenses = {
		{"p", continuarPresent}, {"si", "continúa"}, {"no", "continúes"}

	};

	private static String[][] costarPresent = {
		{"yo", "cuesto"}, {"tu", "cuestas"}, {"el", "cuesta"},
		{"e", "cuestan"}
	};
	private static Object[][] costarTenses = {
		{"p", costarPresent}, {"si", "cuesta"}, {"no", "cuestes"}
	};

	private static String[][] creerPreterite = {
		{"tu", "creíste"}, {"el", "creyó"}, {"n", "creímos"},
		{"e", "creyeron"}
	};
	private static Object[][] creerTenses = {
		{"r", creerPreterite}
	};

	private static String[][] cruzarPreterite = { {"yo", "crucé"} };
	private static Object[][] cruzarTenses = {
		{"r", cruzarPreterite}, {"no", "cruces"}
	};
	
	private static String[][] darPresent = { {"yo", "doy"} };
	private static String[][] darPreterite = {
		{"yo", "di"}, {"tu", "diste"}, {"el", "dio"}, {"n", "dimos"},
		{"e", "dieron"}
	};
	private static Object[][] darTenses = {
		{"p", darPresent}, {"r", darPreterite}
	};
	
	private static String[][] decirPresent = {
		{"yo", "digo"}, {"tu", "dices"}, {"el", "dice"},
		{"e", "dicen"}
	};
	private static String[][] decirPreterite = {
		{"yo", "dije"}, {"tu", "dijiste"}, {"el", "dijo"},
		{"n", "dijimos"}, {"e", "dijeron"}
	};
	private static Object[][] decirTenses = {
		{"p", decirPresent}, {"r", decirPreterite},
		{"f", "dir"}, {"si", "di"}, {"no", "digas"}
	};
	
	private static String[][] descargarPreterite = { {"yo", "descargué"} };
	private static Object[][] descargarTenses = {
		{"r", descargarPreterite}, {"no", "descargues"}
	};

	private static String[][] despedirPresent = {
		{"yo", "despido"}, {"tu", "despides"}, {"el", "despide"},
		{"e", "despiden"}
	};
	private static String[][] despedirPreterite = {
		{"el", "despidió"}
	};
	private static Object[][] despedirTenses = {
		{"p", despedirPresent}, {"r", despedirPreterite},
		{"si", "despide"}, {"no", "despidas"}
	};
	
	private static String[][] despertarPresent = {
		{"yo", "despierto"}, {"tu", "despiertas"}, {"el", "despierta"},
		{"e", "despiertan"}
	};
	private static Object[][] despertarTenses = {
		{"p", despertarPresent}, {"si", "despierta"}, {"no", "despiertes"}
	};
	
	private static String[][] dirigirPresent = { {"yo", "dirijo"} };
	private static Object[][] dirigirTenses = {
		{"p", dirigirPresent}, {"no", "dirijas"}
	};
	
	private static String[][] dolerPresent = {
		{"yo", "duelo"}, {"tu", "dueles"},
		{"el", "duele"}, {"e", "duelen"}
	};
	private static Object[][] dolerTenses = {
		{"p", dolerPresent}, {"si", "duele"}, {"no", "duelas"}
	};

	private static String[][] dormirPresent = {
		{"yo", "duermo"}, {"tu", "duermes"}, {"el", "duerme"},
		{"e", "duermen"}
	};
	private static String[][] dormirPreterite = {
		{"el", "durmió"}, {"e", "durmieron"}
	};
	private static Object[][] dormirTenses = {
		{"p", dormirPresent}, {"r", dormirPreterite},
		{"si", "duerme"}, {"no", "duermas"}
	};
	
	private static String[][] encontrarPresent = {
		{"yo", "encuentro"}, {"tu", "encuentras"}, {"el", "encuentra"},
		{"e", "encuentran"}
	};
	private static Object[][] encontrarTenses = {
		{"p", encontrarPresent},
		{"si", "encuentra"}, {"no", "encuentres"}
	};
	private static String[][] ejercerPresent = {
		{"yo", "ejerzo"}
	};
	private static Object[][] ejercerTenses = {
		{"p", ejercerPresent}, {"no", "ejerzas"}
	};

	private static String[][] entenderPresent = {
		{"yo", "entiendo"}, {"tu", "entiendes"},
		{"el", "entiende"}, {"e", "entienden"}
	};
	private static Object[][] entenderTenses = {
		{"p", entenderPresent}, {"si", "entiende"}, {"no", "entiendas"}
	};

	private static String[][] enviarPresent = {
		{"yo", "envío"}, {"tu", "envías"},
		{"el", "envía"}, {"e", "envían"}
	};
	private static Object[][] enviarTenses = {
		{"p", enviarPresent}, {"si", "envía"}, {"no", "envíes"}
	};

    private static String[][] escogerPresent = {
            {"yo", "escojo"}
    };
    private static Object[][] escogerTenses = {
            {"p", escogerPresent}, {"no", "escojas"}
    };

	private static String[][] estarPresent = {
		{"yo", "estoy"}, {"tu", "estás"}, {"el", "está"},
		{"n", "estamos"}, {"e", "están"}
	};
	private static String[][] estarPreterite = {
		{"yo", "estuve"}, {"tu", "estuviste"}, {"el", "estuvo"},
		{"n", "estuvimos"}, {"e", "estuvieron"}
	};
	private static Object[][] estarTenses = {
		{"p", estarPresent}, {"r", estarPreterite},
		{"si", "está"}, {"no", "estés"}
	};
	
	private static String[][] haberPresent = {
		{"yo", "he"}, {"tu", "has"}, {"el", "ha"},
		{"n", "hemos"}, {"e", "han"}
	};
	private static String[][] haberPreterite = {
		{"yo", "hube"}, {"tu", "hubiste"}, {"el", "hubo"},
		{"n", "hubimos"}, {"e", "hubieron"}
	};
	private static Object[][] haberTenses = {
		{"p", haberPresent}, {"r", haberPreterite},
		{"f", "habr"}, {"no", "hayas"}
	};
	
	private static String[][] hacerPresent = { {"yo", "hago"} };
	private static String[][] hacerPreterite = {
		{"yo", "hice"}, {"tu", "hiciste"}, {"el", "hizo"},
		{"n", "hicimos"}, {"e", "hicieron"}
	};
	private static Object[][] hacerTenses = {
		{"p", hacerPresent}, {"r", hacerPreterite},
		{"f", "har"}, {"si", "haz"}, {"no", "hagas"}
	};

	private static String[][] helarPresent = {
		{"yo", "hielo"}, {"tu", "hielas"},
		{"el", "hiela"}, {"e", "hielan"}
	};
	private static Object[][] helarTenses = {
		{"p", helarPresent}, {"si", "hiela"}, {"no", "hieles"}
	};

	private static String[][] irPresent = {
		{"yo", "voy"}, {"tu", "vas"}, {"el", "va"},
		{"n", "vamos"}, {"e", "ven"}
	};
	private static String[][] irPreterite = {
		{"yo", "fui"}, {"tu", "fuiste"}, {"el", "fue"},
		{"n", "fuimos"}, {"e", "fueron"}
	};
	private static String[][] irImperfect = {
		{"yo", "iba"}, {"tu", "ibas"}, {"el", "iba"},
		{"n", "íbamos"},  {"e", "iban"}
	};
	private static Object[][] irTenses = {
		{"p", irPresent}, {"r", irPreterite}, {"i", irImperfect},
		{"si", "ve"}, {"no", "vayas"}
	};
	
	private static String[][] jugarPresent = {
		{"yo", "juego"}, {"tu", "juegas"}, {"el", "juega"},
		{"n", "jugamos"}, {"e", "juegan"}
	};
	private static String[][] jugarPreterite = { {"yo", "jugué"} };
	private static Object[][] jugarTenses = {
		{"p", jugarPresent}, {"r", jugarPreterite},
		{"si", "juega"}, {"no", "juegues"}
	};
	
	private static String[][] leerPreterite = {
		{"tu", "leíste"}, {"el", "leyó"}, {"n", "leímos"},
		{"e", "leyeron"}
	};
	private static Object[][] leerTenses = {
		{"r", leerPreterite}
	};
	
	private static String[][] llegarPreterite = { {"yo", "llegué"} };
	private static Object[][] llegarTenses = {
		{"r", llegarPreterite}, {"no", "llegues"}
	};
	
	private static String[][] lloverPresent = {
		{"yo", "lluevo"}, {"tu", "llueves"},
		{"el", "llueve"}, {"e", "llueven"}
	};
	private static Object[][] lloverTenses = {
		{"p", lloverPresent}, {"si", "llueve"}, {"no", "lluevas"}
	};
	
	private static String[][] merecerPresent = { {"yo", "merezco"} };
	private static Object[][] merecerTenses = {
		{"p", merecerPresent}, {"no", "merezcas"}
	};
	
	private static String[][] morirPresent = {
		{"yo", "muero"}, {"tu", "mueres"}, {"el", "muere"},
		{"e", "mueren"}
	};
	private static String[][] morirPreterite = { 
		{"el", "murió"}, {"e", "murieron"}
	};
	private static Object[][] morirTenses = {
		{"p", morirPresent}, {"r", morirPreterite},
		{"si", "muere"}, {"no", "mueras"}
	};

	private static String[][] mostrarPresent = {
		{"yo", "muestro"}, {"tu", "muestras"},
		{"el", "muestra"}, {"e", "muestran"}
	};
	private static Object[][] mostrarTenses = {
		{"p", mostrarPresent}, {"si", "muestra"}, {"no", "muestres"}
	};

	private static String[][] nevarPresent = {
		{"yo", "nievo"}, {"tu", "nievas"}, {"el", "nieva"},
		{"e", "nievan"}
	};
	private static Object[][] nevarTenses = {
		{"p", nevarPresent}, {"si", "nieva"}, {"no", "nieves"}
	};

	private static String[][] oirPresent = {
		{"yo", "oigo"}, {"tu", "oyes"}, {"el", "oye"}, {"n", "oímos"},
		{"e", "oyen"}
	};
	private static String[][] oirPreterite = {
		{"tu", "oíste"}, {"el", "oyó"}, {"n", "oímos"}, {"e", "oyeron"}
	};
	private static Object[][] oirTenses = {
		{"p", oirPresent}, {"r", oirPreterite}, {"f", "oir"},
		{"si", "oye"}, {"no", "oigas"}
	};

	private static String[][] parecerPresent = { {"yo", "parezco"} };
	private static Object[][] parecerTenses = {
		{"p", parecerPresent}, {"no", "parezcas"}
	};

	private static String[][] padecerPresent = { {"yo", "padezco"} };
	private static Object[][] padecerTenses = {
		{"p", padecerPresent}, {"no", "padezcas"}
	};

	private static String[][] pedirPresent = {
		{"yo", "pido"}, {"tu", "pides"}, {"el", "pide"}, {"e", "piden"}
	};
	private static String[][] pedirPreterite = {
		{"el", "pidió"}, {"e", "pidieron"}
	};
	private static Object[][] pedirTenses = {
		{"p", pedirPresent}, {"r", pedirPreterite},
		{"si", "pide"}, {"no", "pidas"}
	};

	private static String[][] pegarPreterite = { {"yo", "pegué"} };
	private static Object[][] pegarTenses = {
		{"r", pegarPreterite}, {"no", "pegues"}
	};
	
	private static String[][] pensarPresent = {
		{"yo", "pienso"}, {"tu", "piensas"}, {"el", "piensa"}, {"e", "piensan"}
	};
	private static Object[][] pensarTenses = {
		{"p", pensarPresent}, {"si", "piensa"}, {"no", "pienses"}
	};

	private static String[][] perderPresent = {
		{"yo", "pierdo"}, {"tu", "pierdes"}, {"el", "pierde"}, {"e", "pierden"}
	};
	private static Object[][] perderTenses = {
		{"p", perderPresent},
		{"si", "pierde"}, {"no", "pierdas"}
	};

	private static String[][] pescarPreterite = {
		{"yo", "pesqué"}
	};
	private static Object[][] pescarTenses = {
		{"r", pescarPreterite}, {"no", "pesques"}
	};

	private static String[][] poderPresent = {
		{"yo", "puedo"}, {"tu", "puedes"}, {"el", "puede"}, {"e", "pueden"}
	};
	private static String[][] poderPreterite = {
		{"yo", "pude"}, {"tu", "pudiste"}, {"el", "pudo"},
		{"n", "pudimos"}, {"e", "pudieron"}
	};
	private static Object[][] poderTenses = {
		{"p", poderPresent}, {"r", poderPreterite},
		{"f", "podr"}, {"si", "puede"}, {"no", "puedas"}
	};

	private static String[][] ponerPresent = { {"yo", "pongo"} };
	private static String[][] ponerPreterite = {
		{"yo", "puse"}, {"tu", "pusiste"}, {"el", "puso"},
		{"n", "pusimos"}, {"e", "pusieron"}
	};
	private static Object[][] ponerTenses = {
		{"p", ponerPresent}, {"r", ponerPreterite},
		{"f", "pondr"}, {"si", "pon"}, {"no", "pongas"}
	};

	private static String[][] preferirPresent = {
		{"yo", "prefiero"}, {"tu", "prefieres"}, {"el", "prefiere"},
		{"e", "prefieren"}
	};
	private static String[][] preferirPreterite = {
		{"el", "prefirió"}, {"e", "prefirieron"}
	};
	private static Object[][] preferirTenses = {
		{"p", preferirPresent}, {"r", preferirPreterite},
		{"si", "prefiere"}, {"no", "prefieras"}
	};

	private static String[][] prevenirPresent = {
		{"yo", "prevengo"}, {"tu", "previenes"}, {"el", "previene"},
		{"e", "previenen"}
	};
	private static String[][] prevenirPreterite = {
		{"yo", "previne"}, {"tu", "previniste"}, {"el", "previno"},
		{"n", "previnimos"}, {"e", "previnieron"}
	};
	private static Object[][] prevenirTenses = {
		{"p", prevenirPresent}, {"r", prevenirPreterite}, {"f", "prevendr"},
		{"si", "prevén"}, {"no", "prevengas"}
	};

	private static String[][] quebrarPresent = {
		{"yo", "quiebro"}, {"tu", "quiebras"}, {"el", "quiebra"},
		{"e", "quiebran"}
		};
	private static Object[][] quebrarTenses = {
		{"p", quebrarPresent}, {"si", "quiebra"}, {"no", "quiebres"}
	};

	private static String[][] quererPresent = {
		{"yo", "quiero"}, {"tu", "quieres"},
		{"el", "quiere"}, {"e", "quieren"}
	};
	private static String[][] quererPreterite = {
		{"yo", "quise"}, {"tu", "quisiste"}, {"el", "quiso"},
		{"n", "quisimos"}, {"e", "quisieron"}
	};
	private static Object[][] quererTenses = {
		{"p", quererPresent}, {"r", quererPreterite},
		{"f", "querr"}, {"si", "quiere"}, {"no", "quieras"}
	};

	private static String[][] recordarPresent = {
		{"yo", "recuerdo"}, {"tu", "recuerdas"}, {"el", "recuerda"},
		{"e", "recuerdan"}
	};
	private static Object[][] recordarTenses = {
		{"p", recordarPresent}, {"si", "recuerda"}, {"no", "recuerdes"}
	};

	private static String[][] reirPresent = {
		{"yo", "río"}, {"tu", "ríes"}, {"el", "ríe"}, {"n", "reímos"},
		{"e", "ríen"}
	};
	private static String[][] reirPreterite = {
		{"tu", "reíste"}, {"el", "rió"}, {"n", "reímos"}, {"e", "rieron"}

	};
	private static Object[][] reirTenses = {
		{"p", reirPresent}, {"r", reirPreterite},
		{"f", "reir"}, {"si", "ríe"}, {"no", "rías"}
	};

	private static String[][] renirPresent = {
		{"yo", "riño"}, {"tu", "riñes"}, {"el", "riñe"},
		{"e", "riñen"}
	};
	private static String[][] renirPreterite = {
		{"el", "riñó"}, {"e", "riñeron"}

	};
	private static Object[][] renirTenses = {
		{"p", renirPresent}, {"r", renirPreterite},
		{"si", "riñe"}, {"no", "riñas"}
	};

	private static String[][] saberPresent = {
		{"yo", "sé"}
	};
	private static String[][] saberPreterite = {
		{"yo", "supe"}, {"tu", "supiste"}, {"el", "supo"},
		{"n", "supimos"}, {"e", "supieron"}
	};
	private static Object[][] saberTenses = {
		{"p", saberPresent}, {"r", saberPreterite},
		{"f", "sabr"}, {"no", "sepas"}
	};

	private static String[][] salirPresent = { {"yo", "salgo"} };
	private static Object[][] salirTenses = {
		{"p", salirPresent}, {"f", "saldr"}, {"si", "sal"}, {"no", "salgas"}
	};

	private static String[][] seguirPresent = {
		{"yo", "sigo"}, {"tu", "sigues"}, {"el", "sigue"},
		{"n", "seguimos"}, {"e", "siguen"}
	};
	private static String[][] seguirPreterite = {
		{"el", "siguió"}, {"e", "siguieron"}
	};
	private static Object[][] seguirTenses = {
		{"p", seguirPresent}, {"r", seguirPreterite},
		{"si", "sigue"}, {"no", "sigas"}
	};

	private static String[][] sentarPresent = {
		{"yo", "siento"}, {"tu", "sientas"},
		{"el", "sienta"}, {"e", "sientan"}
	};
	private static Object[][] sentarTenses = {
		{"p", sentarPresent}, {"si", "sienta"}, {"no", "sientes"}
	};

	private static String[][] serPresent = {
		{"yo", "soy"}, {"tu", "eres"}, {"el", "es"},
		{"n", "somos"}, {"e", "son"}
	};
	private static String[][] serPreterite = {
		{"yo", "fui"}, {"tu", "fuiste"}, {"el", "fue"},
		{"n", "fuimos"}, {"e", "fueron"}
	};
	private static String[][] serImperfect = {
		{"yo", "era"}, {"tu", "eras"}, {"el", "era"},
		{"n", "éramos"}, {"e", "eran"}
	};
	private static Object[][] serTenses = {
		{"p", serPresent}, {"r", serPreterite},
		{"i", serImperfect}, {"si", "sé"}, {"no", "seas"}
	};

	private static String[][] servirPresent = {
		{"yo", "sirvo"}, {"tu", "sirves"}, {"el", "sirve"}, {"e", "sirven"}
	};
	private static String[][] servirPreterite = {
		{"el", "sirvió"}, {"e", "sirvieron"}
	};
	private static Object[][] servirTenses = {
		{"p", servirPresent}, {"r", servirPreterite},
		{"si", "sirve"}, {"no", "sirvas"}
	};
	
	private static String[][] sonarPresent = {
		{"yo", "sueno"}, {"tu", "suenas"},
		{"el", "suena"}, {"e", "suenan"}
	};
	private static Object[][] sonarTenses = {
		{"p", sonarPresent}, {"si", "suena"}, {"no", "suenes"}
	};

	private static String[][] sonreirPresent = {
		{"yo", "sonrío"}, {"tu", "sonríes"}, {"el", "sonríe"},
		{"n", "sonreímos"}, {"e", "sonríen"}
	};
	private static String[][] sonreirPreterite = {
		{"tu", "sonreíste"}, {"el", "sonrió"}, {"n", "sonreímos"}, {"e", "sonrieron"}
	};
	private static Object[][] sonreirTenses = {
		{"p", sonreirPresent}, {"r", sonreirPreterite},
		{"f", "sonreir"}, {"si", "sonríe"}, {"no", "sonrías"}
	};

	private static String[][] tenerPresent = {
		{"yo", "tengo"}, {"tu", "tienes"}, {"el", "tiene"}, {"e", "tienen"}
	};
	private static String[][] tenerPreterite = {
		{"yo", "tuve"}, {"tu", "tuviste"}, {"el", "tuvo"},
		{"n", "tuvimos"}, {"e", "tuvieron"}
	};
	private static Object[][] tenerTenses = {
		{"p", tenerPresent}, {"r", tenerPreterite},
		{"f", "tendr"}, {"si", "ten"}, {"no", "tengas"}
	};

	private static String[][] tocarPreterite = { {"yo", "toqué"} };
	private static Object[][] tocarTenses = {
		{"r", tocarPreterite}, {"no", "toques"}
	};

	private static String[][] torcerPresent = {
		{"yo", "tuerzo"}, {"tu", "tuerces"}, {"el", "tuerce"}, {"e", "tuercen"}
	};
	private static Object[][] torcerTenses = {
		{"p", torcerPresent}, {"si", "tuerce"}, {"no", "tuerzas"}
	};

	private static String[][] traerPresent = { {"yo", "traigo"} };
	private static String[][] traerPreterite = {
		{"yo", "traje"}, {"tu", "trajiste"}, {"el", "trajo"},
		{"n", "trajimos"}, {"e", "trajeron"}
	};
	private static Object[][] traerTenses = {
		{"p", traerPresent}, {"r", traerPreterite}, {"no", "traigas"}
	};

	private static String[][] valerPresent = { {"yo", "valgo"} };
	private static Object[][] valerTenses = {
		{"p", valerPresent}, {"f", "valdr"}, {"si", "val"},
		{"no", "valgas"}
	};

	private static String[][] venirPresent = {
		{"yo", "vengo"}, {"tu", "vienes"}, {"el", "viene"},
		{"e", "vienen"}
	};
	private static String[][] venirPreterite = {
		{"yo", "vine"}, {"tu", "viniste"}, {"el", "vino"},
		{"n", "vinimos"}, {"e", "vinieron"}
	};
	private static Object[][] venirTenses = {
		{"p", venirPresent}, {"r", venirPreterite}, {"f", "vendr"},
		{"si", "ven"}, {"no", "vengas"}
	};

	private static String[][] verPresent = { {"yo", "veo"} };
	private static String[][] verPreterite = {
		{"yo", "vi"}, {"el", "vio"}
	};
	private static String[][] verImperfect = {
		{"yo", "veía"}, {"tu", "veías"}, {"el", "veía"},
		{"n", "veíamos"}, {"e", "veían"}
	};
	private static Object[][] verTenses = {
		{"p", verPresent}, {"r", verPreterite},
		{"i", verImperfect}, {"no", "veas"}
	};

	private static Object[][] irregSpaVerbs = {
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
	private static HashMap<String, IrregularSpaVerb> irregSpaVerbsMap2;
	private static RegularSpaVerb regularERVerb;
	private static RegularSpaVerb regularIRVerb;
	private static RegularSpaVerb regularARVerb;
	
	/******************English verb data*****************************/
	private static String[][] irregEnglish3rdPersonSingularPresent = {
            {"accompany", "accompanies"}, {"be", "is"},
            {"can", "can"}, {"carry", "carries"},
            {"cry", "cries"},
            { "do", "does"}, { "go", "goes" },
            {"have", "has"},
            {"reply", "replies"}, {"study", "studies"},
            {"tidy", "tidies"}, {"try", "tries"},
	};
	private static String[][] irregEnglishPreterites = {
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
	private static String[][] irregEnglishGerundStem = {
		{"chat", "chatt"}, {"die", "dy"}, {"forget", "forgett"},
		{"hit", "hitt"}, {"prefer", "preferr"}, {"put", "putt"},
		{"quarrel", "quarrell"}, {"run", "runn"},
		{"regret", "regrett"}, {"stop", "stopp"}, {"swim", "swimm"}, {"win", "winn"}
	};
	private static HashMap<String, String> irregEnglish3rdPersonSingularPresentMap;
	private static HashMap<String, String> irregEnglishPreteritesMap;
	private static HashMap<String, String> irregEnglishGerundMap;

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
