/**
 *
 */
/************************** MPEG-2 NBC Audio Decoder **************************
 *                                                                           
 * "This software module was originally developed by 
 * AT&T, Dolby Laboratories, Fraunhofer Gesellschaft IIS in the course of 
 * development of the MPEG-2 NBC/MPEG-4 Audio standard ISO/IEC 13818-7, 
 * 14496-1,2 and 3. This software module is an implementation of a part of one or more 
 * MPEG-2 NBC/MPEG-4 Audio tools as specified by the MPEG-2 NBC/MPEG-4 
 * Audio standard. ISO/IEC  gives users of the MPEG-2 NBC/MPEG-4 Audio 
 * standards free license to this software module or modifications thereof for use in 
 * hardware or software products claiming conformance to the MPEG-2 NBC/MPEG-4
 * Audio  standards. Those intending to use this software module in hardware or 
 * software products are advised that this use may infringe existing patents. 
 * The original developer of this software module and his/her company, the subsequent 
 * editors and their companies, and ISO/IEC have no liability for use of this software 
 * module or modifications thereof in an implementation. Copyright is not released for 
 * non MPEG-2 NBC/MPEG-4 Audio conforming products.The original developer
 * retains full right to use the code for his/her  own purpose, assign or donate the 
 * code to a third party and to inhibit third party from using the code for non 
 * MPEG-2 NBC/MPEG-4 Audio conforming products. This copyright notice must
 * be included in all copies or derivative works." 
 * Copyright(c)1996.
 * 
 ******************************************************************************/                                                                          
package org.yestech.multimedia.video.mpeg4.audio.AAC;

/**
 * Tables
 */
final class Tables {
	
	/** Table (4.104 in spec) for the TNS_MAX_BANDS constant depending on AOT, windowing and sampling rate.*/
	static final short tns_max_bands_tab[][][] =
	{ 
		{ 
			{
				31, 31, 34, 40, 42, 51, 46, 46, 42, 42, 42, 39
			},
			{
				9, 9, 10, 14, 14, 14, 14, 14, 14, 14, 14, 14
			},
		},
		{ 
			{
				28, 28, 27, 26, 26, 26, 29, 29, 23, 23, 23, 19
			},
			{
				7, 7, 7, 6, 6, 6, 7, 7, 8, 8, 8, 7
			},
		}
	};		

	static final short sfb_96_1024[] =
	{
			4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 
			64, 72, 80, 88, 96, 108, 120, 132, 144, 156, 172, 188, 212,
			240, 276, 320, 384, 448, 512, 576, 640, 704, 768, 832, 896, 960, 1024 
	};

	static final short sfb_96_128[] =
	{
			4, 8, 12, 16, 20, 24, 32, 40, 48, 64, 92, 128
	};
	
	static final short sfb_64_1024[] =
	{
		4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 
		64, 72, 80, 88, 100, 112, 124, 140, 156, 172, 192, 216, 240, 
		268, 304, 344, 384, 424, 464, 504, 544, 584, 624, 664, 704, 744, 
		784, 824, 864, 904, 944, 984, 1024  
	};

	static final short sfb_64_128[] =
	{
		4, 8, 12, 16, 20, 24, 32, 40, 48, 64, 92, 128
	};
	
	static final short sfb_48_1024[] =
	{
			4,	8,	12,	16,	20,	24,	28,	
			32,	36,	40,	48,	56,	64,	72,	
			80,	88,	96,	108,	120,	132,	144,	
			160,	176,	196,	216,	240,	264,	292,	
			320,	352,	384,	416,	448,	480,	512,	
			544,	576,	608,	640,	672,	704,	736,	
			768,	800,	832,	864,	896,	928,	1024
	};

	static final short sfb_48_128[] =
	{
		4,	8,	12,	16,	20,	28,	36,	
		44,	56,	68,	80,	96,	112, 128
	};

	static final short sfb_32_1024[] =
	{
			4,	8,	12,	16,	20,	24,	28,	
			32,	36,	40,	48,	56,	64,	72,	
			80,	88,	96,	108,	120,	132,	144,	
			160,	176,	196,	216,	240,	264,	292,	
			320,	352,	384,	416,	448,	480,	512,	
			544,	576,	608,	640,	672,	704,	736,	
			768,	800,	832,	864,	896,	928,	960,
			992,	1024
	};

	static final short sfb_22_1024[] =
	{
		4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 52, 60, 68, 76, 
		84, 92, 100, 108, 116, 124, 136, 148, 160, 172, 188, 204, 
		220, 240, 260, 284, 308, 336, 364, 396, 432, 468, 508, 552, 
		600, 652, 704, 768, 832, 896, 960, 1024 
	};

	static final short sfb_22_128[] =
	{
		4, 8, 12, 16, 20, 24, 28, 36, 44, 52, 64, 76, 92, 108, 128 
	};

	static final short sfb_16_1024[] =
	{
			8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 
			100, 112, 124, 136, 148, 160, 172, 184, 196, 
			212, 228, 244, 260, 280, 300, 320, 344, 368,
			396, 424, 456, 492, 532, 572, 616, 664, 716,
			772, 832, 896, 960, 1024 
	};

	static final short sfb_16_128[] =
	{
			4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 60, 72, 88, 108, 128
	};
	
	static final short sfb_8_1024[] =
	{
			12, 24, 36, 48, 60, 72, 84, 96, 108, 
			120, 132, 144, 156, 172, 188, 204, 220, 
			236, 252, 268, 288, 308, 328, 348, 372, 
			396, 420, 448, 476, 508, 544, 580, 620, 
			664, 712, 764, 820, 880, 944, 1024 
	};

	static final short sfb_8_128[] =
	{
			4, 8, 12, 16, 20, 24, 28, 36, 44, 52, 60, 72, 88, 108, 128
	};

	static final SR_Info[] samp_rate_info = {
		/* sampling_frequency, #long sfb, long sfb, #short sfb, short sfb */
		/* samp_rate, nsfb1024, SFbands1024, nsfb128, SFbands128 */
		new SR_Info (96000, sfb_96_1024, sfb_96_128), /* 96000 */
		new SR_Info (88200, sfb_96_1024, sfb_96_128), /* 88200 */
		new SR_Info (64000, sfb_64_1024, sfb_64_128), /* 64000 */
		new SR_Info (48000, sfb_48_1024, sfb_48_128), /* 48000 */
		new SR_Info (44100, sfb_48_1024, sfb_48_128), /* 44100 */
		new SR_Info (32000, sfb_32_1024, sfb_48_128), /* 32000 */
		new SR_Info (24000, sfb_22_1024, sfb_22_128), /* 24000 */
		new SR_Info (22050, sfb_22_1024, sfb_22_128), /* 22050 */
		new SR_Info (16000, sfb_16_1024, sfb_16_128), /* 16000 */
		new SR_Info (12000, sfb_16_1024, sfb_16_128), /* 12000 */
		new SR_Info (11025, sfb_16_1024, sfb_16_128), /* 11025 */
		new SR_Info (8000, sfb_8_1024, sfb_8_128),    /* 8000  */
		new SR_Info (0, null, null),   				  /* 7350  */
		new SR_Info (0, null, null),
		new SR_Info (0, null, null),
		new SR_Info (0, null, null)
	};
	
	static final int SampleIndexRateTable[] = {
		96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350, 0, 0, 0
	};

	static final float dol_long[] = {  /* IBLEN = 1024 */
		0.00029256153896361f,
		0.00042998567353047f,
		0.00054674074589540f,
		0.00065482304299792f,
		0.00075870195068747f,
		0.00086059331713336f,
		0.00096177541439010f,
		0.0010630609410878f,
		0.0011650036308132f,
		0.0012680012194148f,
		0.0013723517232956f,
		0.0014782864109136f,
		0.0015859901976719f,
		0.0016956148252373f,
		0.0018072876903517f,
		0.0019211179405514f,
		0.0020372007924215f,
		0.0021556206591754f,
		0.0022764534599614f,
		0.0023997683540995f,
		0.0025256290631156f,
		0.0026540948920831f,
		0.0027852215281403f,
		0.0029190616715331f,
		0.0030556655443223f,
		0.0031950812943391f,
		0.0033373553240392f,
		0.0034825325586930f,
		0.0036306566699199f,
		0.0037817702604646f,
		0.0039359150179719f,
		0.0040931318437260f,
		0.0042534609610026f,
		0.0044169420066964f,
		0.0045836141091341f,
		0.0047535159544086f,
		0.0049266858431214f,
		0.0051031617390698f,
		0.0052829813111335f,
		0.0054661819693975f,
		0.0056528008963682f,
		0.0058428750739943f,
		0.0060364413070882f,
		0.0062335362436492f,
		0.0064341963925079f,
		0.0066384581386503f,
		0.0068463577565218f,
		0.0070579314215715f,
		0.0072732152202559f,
		0.0074922451586909f,
		0.0077150571701162f,
		0.0079416871213115f,
		0.0081721708180857f,
		0.0084065440099458f,
		0.0086448423940363f,
		0.0088871016184291f,
		0.0091333572848345f,
		0.0093836449507939f,
		0.0096380001314086f,
		0.0098964583006517f,
		0.010159054892306f,
		0.010425825300561f,
		0.010696804880310f,
		0.010972028947167f,
		0.011251532777236f,
		0.011535351606646f,
		0.011823520630897f,
		0.012116075003993f,
		0.012413049837429f,
		0.012714480198999f,
		0.013020401111478f,
		0.013330847551161f,
		0.013645854446288f,
		0.013965456675352f,
		0.014289689065314f,
		0.014618586389712f,
		0.014952183366697f,
		0.015290514656976f,
		0.015633614861688f,
		0.015981518520214f,
		0.016334260107915f,
		0.016691874033817f,
		0.017054394638241f,
		0.017421856190380f,
		0.017794292885832f,
		0.018171738844085f,
		0.018554228105962f,
		0.018941794631032f,
		0.019334472294980f,
		0.019732294886947f,
		0.020135296106839f,
		0.020543509562604f,
		0.020956968767488f,
		0.021375707137257f,
		0.021799757987407f,
		0.022229154530343f,
		0.022663929872540f,
		0.023104117011689f,
		0.023549748833816f,
		0.024000858110398f,
		0.024457477495451f,
		0.024919639522613f,
		0.025387376602207f,
		0.025860721018295f,
		0.026339704925726f,
		0.026824360347160f,
		0.027314719170100f,
		0.027810813143900f,
		0.028312673876775f,
		0.028820332832801f,
		0.029333821328905f,
		0.029853170531859f,
		0.030378411455255f,
		0.030909574956490f,
		0.031446691733739f,
		0.031989792322926f,
		0.032538907094693f,
		0.033094066251369f,
		0.033655299823935f,
		0.034222637668991f,
		0.034796109465717f,
		0.035375744712844f,
		0.035961572725616f,
		0.036553622632758f,
		0.037151923373446f,
		0.037756503694277f,
		0.038367392146243f,
		0.038984617081711f,
		0.039608206651398f,
		0.040238188801359f,
		0.040874591269976f,
		0.041517441584950f,
		0.042166767060301f,
		0.042822594793376f,
		0.043484951661852f,
		0.044153864320760f,
		0.044829359199509f,
		0.045511462498913f,
		0.046200200188234f,
		0.046895598002228f,
		0.047597681438201f,
		0.048306475753074f,
		0.049022005960455f,
		0.049744296827725f,
		0.050473372873129f,
		0.051209258362879f,
		0.051951977308273f,
		0.052701553462813f,
		0.053458010319350f,
		0.054221371107223f,
		0.054991658789428f,
		0.055768896059787f,
		0.056553105340134f,
		0.057344308777513f,
		0.058142528241393f,
		0.058947785320893f,
		0.059760101322019f,
		0.060579497264926f,
		0.061405993881180f,
		0.062239611611049f,
		0.063080370600799f,
		0.063928290700012f,
		0.064783391458919f,
		0.065645692125747f,
		0.066515211644086f,
		0.067391968650269f,
		0.068275981470777f,
		0.069167268119652f,
		0.070065846295935f,
		0.070971733381121f,
		0.071884946436630f,
		0.072805502201299f,
		0.073733417088896f,
		0.074668707185649f,
		0.075611388247794f,
		0.076561475699152f,
		0.077518984628715f,
		0.078483929788261f,
		0.079456325589986f,
		0.080436186104162f,
		0.081423525056808f,
		0.082418355827392f,
		0.083420691446553f,
		0.084430544593841f,
		0.085447927595483f,
		0.086472852422178f,
		0.087505330686900f,
		0.088545373642744f,
		0.089592992180780f,
		0.090648196827937f,
		0.091710997744919f,
		0.092781404724131f,
		0.093859427187640f,
		0.094945074185163f,
		0.096038354392069f,
		0.097139276107423f,
		0.098247847252041f,
		0.099364075366580f,
		0.10048796760965f,
		0.10161953075597f,
		0.10275877119451f,
		0.10390569492671f,
		0.10506030756469f,
		0.10622261432949f,
		0.10739262004941f,
		0.10857032915821f,
		0.10975574569357f,
		0.11094887329534f,
		0.11214971520402f,
		0.11335827425914f,
		0.11457455289772f,
		0.11579855315274f,
		0.11703027665170f,
		0.11826972461510f,
		0.11951689785504f,
		0.12077179677383f,
		0.12203442136263f,
		0.12330477120008f,
		0.12458284545102f,
		0.12586864286523f,
		0.12716216177615f,
		0.12846340009971f,
		0.12977235533312f,
		0.13108902455375f,
		0.13241340441801f,
		0.13374549116025f,
		0.13508528059173f,
		0.13643276809961f,
		0.13778794864595f,
		0.13915081676677f,
		0.14052136657114f,
		0.14189959174027f,
		0.14328548552671f,
		0.14467904075349f,
		0.14608024981336f,
		0.14748910466804f,
		0.14890559684750f,
		0.15032971744929f,
		0.15176145713790f,
		0.15320080614414f,
		0.15464775426459f,
		0.15610229086100f,
		0.15756440485987f,
		0.15903408475193f,
		0.16051131859170f,
		0.16199609399712f,
		0.16348839814917f,
		0.16498821779156f,
		0.16649553923042f,
		0.16801034833404f,
		0.16953263053270f,
		0.17106237081842f,
		0.17259955374484f,
		0.17414416342714f,
		0.17569618354193f,
		0.17725559732720f,
		0.17882238758238f,
		0.18039653666830f,
		0.18197802650733f,
		0.18356683858343f,
		0.18516295394233f,
		0.18676635319174f,
		0.18837701650148f,
		0.18999492360384f,
		0.19162005379380f,
		0.19325238592940f,
		0.19489189843209f,
		0.19653856928714f,
		0.19819237604409f,
		0.19985329581721f,
		0.20152130528605f,
		0.20319638069594f,
		0.20487849785865f,
		0.20656763215298f,
		0.20826375852540f,
		0.20996685149083f,
		0.21167688513330f,
		0.21339383310678f,
		0.21511766863598f,
		0.21684836451719f,
		0.21858589311922f,
		0.22033022638425f,
		0.22208133582887f,
		0.22383919254503f,
		0.22560376720111f,
		0.22737503004300f,
		0.22915295089517f,
		0.23093749916189f,
		0.23272864382838f,
		0.23452635346201f,
		0.23633059621364f,
		0.23814133981883f,
		0.23995855159925f,
		0.24178219846403f,
		0.24361224691114f,
		0.24544866302890f,
		0.24729141249740f,
		0.24914046059007f,
		0.25099577217522f,
		0.25285731171763f,
		0.25472504328019f,
		0.25659893052556f,
		0.25847893671788f,
		0.26036502472451f,
		0.26225715701781f,
		0.26415529567692f,
		0.26605940238966f,
		0.26796943845439f,
		0.26988536478190f,
		0.27180714189742f,
		0.27373472994256f,
		0.27566808867736f,
		0.27760717748238f,
		0.27955195536071f,
		0.28150238094021f,
		0.28345841247557f,
		0.28542000785059f,
		0.28738712458038f,
		0.28935971981364f,
		0.29133775033492f,
		0.29332117256704f,
		0.29530994257338f,
		0.29730401606034f,
		0.29930334837974f,
		0.30130789453132f,
		0.30331760916521f,
		0.30533244658452f,
		0.30735236074785f,
		0.30937730527195f,
		0.31140723343430f,
		0.31344209817583f,
		0.31548185210356f,
		0.31752644749341f,
		0.31957583629288f,
		0.32162997012390f,
		0.32368880028565f,
		0.32575227775738f,
		0.32782035320134f,
		0.32989297696566f,
		0.33197009908736f,
		0.33405166929523f,
		0.33613763701295f,
		0.33822795136203f,
		0.34032256116495f,
		0.34242141494820f,
		0.34452446094547f,
		0.34663164710072f,
		0.34874292107143f,
		0.35085823023181f,
		0.35297752167598f,
		0.35510074222129f,
		0.35722783841160f,
		0.35935875652060f,
		0.36149344255514f,
		0.36363184225864f,
		0.36577390111444f,
		0.36791956434930f,
		0.37006877693676f,
		0.37222148360070f,
		0.37437762881878f,
		0.37653715682603f,
		0.37870001161834f,
		0.38086613695607f,
		0.38303547636766f,
		0.38520797315322f,
		0.38738357038821f,
		0.38956221092708f,
		0.39174383740701f,
		0.39392839225157f,
		0.39611581767449f,
		0.39830605568342f,
		0.40049904808370f,
		0.40269473648218f,
		0.40489306229101f,
		0.40709396673153f,
		0.40929739083810f,
		0.41150327546197f,
		0.41371156127524f,
		0.41592218877472f,
		0.41813509828594f,
		0.42035022996702f,
		0.42256752381274f,
		0.42478691965848f,
		0.42700835718423f,
		0.42923177591866f,
		0.43145711524314f,
		0.43368431439580f,
		0.43591331247564f,
		0.43814404844658f,
		0.44037646114161f,
		0.44261048926688f,
		0.44484607140589f,
		0.44708314602359f,
		0.44932165147057f,
		0.45156152598727f,
		0.45380270770813f,
		0.45604513466581f,
		0.45828874479543f,
		0.46053347593880f,
		0.46277926584861f,
		0.46502605219277f,
		0.46727377255861f,
		0.46952236445718f,
		0.47177176532752f,
		0.47402191254100f,
		0.47627274340557f,
		0.47852419517009f,
		0.48077620502869f,
		0.48302871012505f,
		0.48528164755674f,
		0.48753495437962f,
		0.48978856761212f,
		0.49204242423966f,
		0.49429646121898f,
		0.49655061548250f,
		0.49880482394273f,
		0.50105902349665f,
		0.50331315103004f,
		0.50556714342194f,
		0.50782093754901f,
		0.51007447028990f,
		0.51232767852971f,
		0.51458049916433f,
		0.51683286910489f,
		0.51908472528213f,
		0.52133600465083f,
		0.52358664419420f,
		0.52583658092832f,
		0.52808575190648f,
		0.53033409422367f,
		0.53258154502092f,
		0.53482804148974f,
		0.53707352087652f,
		0.53931792048690f,
		0.54156117769021f,
		0.54380322992385f,
		0.54604401469766f,
		0.54828346959835f,
		0.55052153229384f,
		0.55275814053768f,
		0.55499323217338f,
		0.55722674513883f,
		0.55945861747062f,
		0.56168878730842f,
		0.56391719289930f,
		0.56614377260214f,
		0.56836846489188f,
		0.57059120836390f,
		0.57281194173835f,
		0.57503060386439f,
		0.57724713372458f,
		0.57946147043912f,
		0.58167355327012f,
		0.58388332162591f,
		0.58609071506528f,
		0.58829567330173f,
		0.59049813620770f,
		0.59269804381879f,
		0.59489533633802f,
		0.59708995413996f,
		0.59928183777495f,
		0.60147092797329f,
		0.60365716564937f,
		0.60584049190582f,
		0.60802084803764f,
		0.61019817553632f,
		0.61237241609393f,
		0.61454351160718f,
		0.61671140418155f,
		0.61887603613527f,
		0.62103735000336f,
		0.62319528854167f,
		0.62534979473088f,
		0.62750081178042f,
		0.62964828313250f,
		0.63179215246597f,
		0.63393236370030f,
		0.63606886099946f,
		0.63820158877577f,
		0.64033049169379f,
		0.64245551467413f,
		0.64457660289729f,
		0.64669370180740f,
		0.64880675711607f,
		0.65091571480603f,
		0.65302052113494f,
		0.65512112263906f,
		0.65721746613689f,
		0.65930949873289f,
		0.66139716782102f,
		0.66348042108842f,
		0.66555920651892f,
		0.66763347239664f,
		0.66970316730947f,
		0.67176824015260f,
		0.67382864013196f,
		0.67588431676768f,
		0.67793521989751f,
		0.67998129968017f,
		0.68202250659876f,
		0.68405879146403f,
		0.68609010541774f,
		0.68811639993588f,
		0.69013762683195f,
		0.69215373826012f,
		0.69416468671849f,
		0.69617042505214f,
		0.69817090645634f,
		0.70016608447958f,
		0.70215591302664f,
		0.70414034636163f,
		0.70611933911096f,
		0.70809284626630f,
		0.71006082318751f,
		0.71202322560554f,
		0.71398000962530f,
		0.71593113172842f,
		0.71787654877613f,
		0.71981621801195f,
		0.72175009706445f,
		0.72367814394990f,
		0.72560031707496f,
		0.72751657523927f,
		0.72942687763803f,
		0.73133118386457f,
		0.73322945391280f,
		0.73512164817975f,
		0.73700772746796f,
		0.73888765298787f,
		0.74076138636020f,
		0.74262888961827f,
		0.74449012521027f,
		0.74634505600152f,
		0.74819364527663f,
		0.75003585674175f,
		0.75187165452661f,
		0.75370100318668f,
		0.75552386770515f,
		0.75734021349500f,
		0.75915000640095f,
		0.76095321270137f,
		0.76274979911019f,
		0.76453973277875f,
		0.76632298129757f,
		0.76809951269819f,
		0.76986929545481f,
		0.77163229848604f,
		0.77338849115651f,
		0.77513784327849f,
		0.77688032511340f,
		0.77861590737340f,
		0.78034456122283f,
		0.78206625827961f,
		0.78378097061667f,
		0.78548867076330f,
		0.78718933170643f,
		0.78888292689189f,
		0.79056943022564f,
		0.79224881607494f,
		0.79392105926949f,
		0.79558613510249f,
		0.79724401933170f,
		0.79889468818046f,
		0.80053811833858f,
		0.80217428696334f,
		0.80380317168028f,
		0.80542475058405f,
		0.80703900223920f,
		0.80864590568089f,
		0.81024544041560f,
		0.81183758642175f,
		0.81342232415032f,
		0.81499963452540f,
		0.81656949894467f,
		0.81813189927991f,
		0.81968681787738f,
		0.82123423755821f,
		0.82277414161874f,
		0.82430651383076f,
		0.82583133844180f,
		0.82734860017528f,
		0.82885828423070f,
		0.83036037628369f,
		0.83185486248609f,
		0.83334172946597f,
		0.83482096432759f,
		0.83629255465130f,
		0.83775648849344f,
		0.83921275438615f,
		0.84066134133716f,
		0.84210223882952f,
		0.84353543682130f,
		0.84496092574524f,
		0.84637869650833f,
		0.84778874049138f,
		0.84919104954855f,
		0.85058561600677f,
		0.85197243266520f,
		0.85335149279457f,
		0.85472279013653f,
		0.85608631890295f,
		0.85744207377513f,
		0.85879004990298f,
		0.86013024290422f,
		0.86146264886346f,
		0.86278726433124f,
		0.86410408632306f,
		0.86541311231838f,
		0.86671434025950f,
		0.86800776855046f,
		0.86929339605590f,
		0.87057122209981f,
		0.87184124646433f,
		0.87310346938840f,
		0.87435789156650f,
		0.87560451414719f,
		0.87684333873173f,
		0.87807436737261f,
		0.87929760257204f,
		0.88051304728038f,
		0.88172070489456f,
		0.88292057925645f,
		0.88411267465117f,
		0.88529699580537f,
		0.88647354788545f,
		0.88764233649580f,
		0.88880336767692f,
		0.88995664790351f,
		0.89110218408260f,
		0.89223998355154f,
		0.89337005407600f,
		0.89449240384793f,
		0.89560704148345f,
		0.89671397602074f,
		0.89781321691786f,
		0.89890477405053f,
		0.89998865770993f,
		0.90106487860034f,
		0.90213344783689f,
		0.90319437694315f,
		0.90424767784873f,
		0.90529336288690f,
		0.90633144479201f,
		0.90736193669708f,
		0.90838485213119f,
		0.90940020501694f,
		0.91040800966776f,
		0.91140828078533f,
		0.91240103345685f,
		0.91338628315231f,
		0.91436404572173f,
		0.91533433739238f,
		0.91629717476594f,
		0.91725257481564f,
		0.91820055488334f,
		0.91914113267664f,
		0.92007432626589f,
		0.92100015408120f,
		0.92191863490944f,
		0.92282978789113f,
		0.92373363251740f,
		0.92463018862687f,
		0.92551947640245f,
		0.92640151636824f,
		0.92727632938624f,
		0.92814393665320f,
		0.92900435969727f,
		0.92985762037477f,
		0.93070374086684f,
		0.93154274367610f,
		0.93237465162328f,
		0.93319948784382f,
		0.93401727578443f,
		0.93482803919967f,
		0.93563180214841f,
		0.93642858899043f,
		0.93721842438279f,
		0.93800133327637f,
		0.93877734091223f,
		0.93954647281807f,
		0.94030875480458f,
		0.94106421296182f,
		0.94181287365556f,
		0.94255476352362f,
		0.94328990947213f,
		0.94401833867184f,
		0.94474007855439f,
		0.94545515680855f,
		0.94616360137644f,
		0.94686544044975f,
		0.94756070246592f,
		0.94824941610434f,
		0.94893161028248f,
		0.94960731415209f,
		0.95027655709525f,
		0.95093936872056f,
		0.95159577885924f,
		0.95224581756115f,
		0.95288951509097f,
		0.95352690192417f,
		0.95415800874314f,
		0.95478286643320f,
		0.95540150607863f,
		0.95601395895871f,
		0.95662025654373f,
		0.95722043049100f,
		0.95781451264084f,
		0.95840253501260f,
		0.95898452980058f,
		0.95956052937008f,
		0.96013056625336f,
		0.96069467314557f,
		0.96125288290073f,
		0.96180522852773f,
		0.96235174318622f,
		0.96289246018262f,
		0.96342741296604f,
		0.96395663512424f,
		0.96448016037959f,
		0.96499802258499f,
		0.96551025571985f,
		0.96601689388602f,
		0.96651797130376f,
		0.96701352230768f,
		0.96750358134269f,
		0.96798818295998f,
		0.96846736181297f,
		0.96894115265327f,
		0.96940959032667f,
		0.96987270976912f,
		0.97033054600270f,
		0.97078313413161f,
		0.97123050933818f,
		0.97167270687887f,
		0.97210976208030f,
		0.97254171033525f,
		0.97296858709871f,
		0.97339042788392f,
		0.97380726825843f,
		0.97421914384017f,
		0.97462609029350f,
		0.97502814332534f,
		0.97542533868127f,
		0.97581771214160f,
		0.97620529951759f,
		0.97658813664749f,
		0.97696625939282f,
		0.97733970363445f,
		0.97770850526884f,
		0.97807270020427f,
		0.97843232435704f,
		0.97878741364771f,
		0.97913800399743f,
		0.97948413132414f,
		0.97982583153895f,
		0.98016314054243f,
		0.98049609422096f,
		0.98082472844313f,
		0.98114907905608f,
		0.98146918188197f,
		0.98178507271438f,
		0.98209678731477f,
		0.98240436140902f,
		0.98270783068385f,
		0.98300723078342f,
		0.98330259730589f,
		0.98359396579995f,
		0.98388137176152f,
		0.98416485063031f,
		0.98444443778651f,
		0.98472016854752f,
		0.98499207816463f,
		0.98526020181980f,
		0.98552457462240f,
		0.98578523160609f,
		0.98604220772560f,
		0.98629553785362f,
		0.98654525677772f,
		0.98679139919726f,
		0.98703399972035f,
		0.98727309286089f,
		0.98750871303556f,
		0.98774089456089f,
		0.98796967165036f,
		0.98819507841154f,
		0.98841714884323f,
		0.98863591683269f,
		0.98885141615285f,
		0.98906368045957f,
		0.98927274328896f,
		0.98947863805473f,
		0.98968139804554f,
		0.98988105642241f,
		0.99007764621618f,
		0.99027120032501f,
		0.99046175151186f,
		0.99064933240208f,
		0.99083397548099f,
		0.99101571309153f,
		0.99119457743191f,
		0.99137060055337f,
		0.99154381435784f,
		0.99171425059582f,
		0.99188194086414f,
		0.99204691660388f,
		0.99220920909823f,
		0.99236884947045f,
		0.99252586868186f,
		0.99268029752989f,
		0.99283216664606f,
		0.99298150649419f,
		0.99312834736847f,
		0.99327271939167f,
		0.99341465251338f,
		0.99355417650825f,
		0.99369132097430f,
		0.99382611533130f,
		0.99395858881910f,
		0.99408877049612f,
		0.99421668923778f,
		0.99434237373503f,
		0.99446585249289f,
		0.99458715382906f,
		0.99470630587254f,
		0.99482333656229f,
		0.99493827364600f,
		0.99505114467878f,
		0.99516197702200f,
		0.99527079784214f,
		0.99537763410962f,
		0.99548251259777f,
		0.99558545988178f,
		0.99568650233767f,
		0.99578566614138f,
		0.99588297726783f,
		0.99597846149005f,
		0.99607214437834f,
		0.99616405129947f,
		0.99625420741595f,
		0.99634263768527f,
		0.99642936685928f,
		0.99651441948352f,
		0.99659781989663f,
		0.99667959222978f,
		0.99675976040620f,
		0.99683834814063f,
		0.99691537893895f,
		0.99699087609774f,
		0.99706486270391f,
		0.99713736163442f,
		0.99720839555593f,
		0.99727798692461f,
		0.99734615798589f,
		0.99741293077431f,
		0.99747832711337f,
		0.99754236861541f,
		0.99760507668158f,
		0.99766647250181f,
		0.99772657705478f,
		0.99778541110799f,
		0.99784299521785f,
		0.99789934972976f,
		0.99795449477828f,
		0.99800845028730f,
		0.99806123597027f,
		0.99811287133042f,
		0.99816337566108f,
		0.99821276804596f,
		0.99826106735952f,
		0.99830829226732f,
		0.99835446122649f,
		0.99839959248609f,
		0.99844370408765f,
		0.99848681386566f,
		0.99852893944805f,
		0.99857009825685f,
		0.99861030750869f,
		0.99864958421549f,
		0.99868794518504f,
		0.99872540702178f,
		0.99876198612738f,
		0.99879769870160f,
		0.99883256074295f,
		0.99886658804953f,
		0.99889979621983f,
		0.99893220065356f,
		0.99896381655254f,
		0.99899465892154f,
		0.99902474256924f,
		0.99905408210916f,
		0.99908269196056f,
		0.99911058634952f,
		0.99913777930986f,
		0.99916428468421f,
		0.99919011612505f,
		0.99921528709576f,
		0.99923981087174f,
		0.99926370054150f,
		0.99928696900779f,
		0.99930962898876f,
		0.99933169301910f,
		0.99935317345126f,
		0.99937408245662f,
		0.99939443202674f,
		0.99941423397457f,
		0.99943349993572f,
		0.99945224136972f,
		0.99947046956130f,
		0.99948819562171f,
		0.99950543049000f,
		0.99952218493439f,
		0.99953846955355f,
		0.99955429477803f,
		0.99956967087154f,
		0.99958460793242f,
		0.99959911589494f,
		0.99961320453077f,
		0.99962688345035f,
		0.99964016210433f,
		0.99965304978499f,
		0.99966555562769f,
		0.99967768861231f,
		0.99968945756473f,
		0.99970087115825f,
		0.99971193791510f,
		0.99972266620792f,
		0.99973306426121f,
		0.99974314015288f,
		0.99975290181568f,
		0.99976235703876f,
		0.99977151346914f,
		0.99978037861326f,
		0.99978895983845f,
		0.99979726437448f,
		0.99980529931507f,
		0.99981307161943f,
		0.99982058811377f,
		0.99982785549283f,
		0.99983488032144f,
		0.99984166903600f,
		0.99984822794606f,
		0.99985456323584f,
		0.99986068096572f,
		0.99986658707386f,
		0.99987228737764f,
		0.99987778757524f,
		0.99988309324717f,
		0.99988820985777f,
		0.99989314275675f,
		0.99989789718072f,
		0.99990247825468f,
		0.99990689099357f,
		0.99991114030376f,
		0.99991523098456f,
		0.99991916772971f,
		0.99992295512891f,
		0.99992659766930f,
		0.99993009973692f,
		0.99993346561824f,
		0.99993669950161f,
		0.99993980547870f,
		0.99994278754604f,
		0.99994564960642f,
		0.99994839547033f,
		0.99995102885747f,
		0.99995355339809f,
		0.99995597263451f,
		0.99995829002249f,
		0.99996050893264f,
		0.99996263265183f,
		0.99996466438460f,
		0.99996660725452f,
		0.99996846430558f,
		0.99997023850356f,
		0.99997193273736f,
		0.99997354982037f,
		0.99997509249183f,
		0.99997656341810f,
		0.99997796519400f,
		0.99997930034415f,
		0.99998057132421f,
		0.99998178052220f,
		0.99998293025975f,
		0.99998402279338f,
		0.99998506031574f,
		0.99998604495686f,
		0.99998697878536f,
		0.99998786380966f,
		0.99998870197921f,
		0.99998949518567f,
		0.99999024526408f,
		0.99999095399401f,
		0.99999162310077f,
		0.99999225425649f,
		0.99999284908128f,
		0.99999340914435f,
		0.99999393596510f,
		0.99999443101421f,
		0.99999489571473f,
		0.99999533144314f,
		0.99999573953040f,
		0.99999612126300f,
		0.99999647788395f,
		0.99999681059383f,
		0.99999712055178f,
		0.99999740887647f,
		0.99999767664709f,
		0.99999792490431f,
		0.99999815465123f,
		0.99999836685427f,
		0.99999856244415f,
		0.99999874231676f,
		0.99999890733405f,
		0.99999905832493f,
		0.99999919608613f,
		0.99999932138304f,
		0.99999943495056f,
		0.99999953749392f,
		0.99999962968950f,
		0.99999971218563f,
		0.99999978560337f,
		0.99999985053727f,
		0.99999990755616f,
		0.99999995720387f
	};


	static final float dol_short [] = {  /* 128 pt half window */
		4.3795702929468881e-005f,
		0.00011867384265436617f,
		0.0002307165763996192f,
		0.00038947282760568383f,
		0.00060581272288302553f,
		0.00089199695169487453f,
		0.0012617254423430522f,
		0.0017301724373162003f,
		0.0023140071937421476f,
		0.0030313989666022221f,
		0.0039020049735530842f,
		0.0049469401815512024f,
		0.0061887279335368318f,
		0.0076512306364647726f,
		0.0093595599562652423f,
		0.011339966208377799f,
		0.013619706891715299f,
		0.016226894586323766f,
		0.019190324717288168f,
		0.022539283975960878f,
		0.026303340480472455f,
		0.030512117046644357f,
		0.03519504922365594f,
		0.040381130021856941f,
		0.046098643518702249f,
		0.052374889768730587f,
		0.059235903660769147f,
		0.066706170556282418f,
		0.074808341703430481f,
		0.083562952548726227f,
		0.092988147159339674f,
		0.1030994120216919f,
		0.11390932249409955f,
		0.12542730516149531f,
		0.13765941926783826f,
		0.15060816028651081f,
		0.16427228853114245f,
		0.17864668550988483f,
		0.19372224048676889f,
		0.20948576943658073f,
		0.22591996826744942f,
		0.24300340184133981f,
		0.26071052995068139f,
		0.27901177101369551f,
		0.29787360383626599f,
		0.3172587073594233f,
		0.33712613787396362f,
		0.35743154274286698f,
		0.37812740923363009f,
		0.39916334663203618f,
		0.42048639939189658f,
		0.4420413886774246f,
		0.4637712792815169f,
		0.4856175685594023f,
		0.50752069370766872f,
		0.52942045344797806f,
		0.55125643994680196f,
		0.57296847662071559f,
		0.59449705734411495f,
		0.61578378249506627f,
		0.63677178724712891f,
		0.65740615754163356f,
		0.67763432925662526f,
		0.69740646622548552f,
		0.71667581294953808f,
		0.73539901809352737f,
		0.75353642514900732f,
		0.77105232699609816f,
		0.78791518148597028f,
		0.80409778560147072f,
		0.81957740622770781f,
		0.83433586607383625f,
		0.84835958382689225f,
		0.86163956818294229f,
		0.87417136598406997f,
		0.88595496528524853f,
		0.89699465477567619f,
		0.90729884157670959f,
		0.91687983002436779f,
		0.92575356460899649f,
		0.93393934077779084f,
		0.94145948779657318f,
		0.94833902830402828f,
		0.95460531956280026f,
		0.96028768170574896f,
		0.96541701848104766f,
		0.97002543610646474f,
		0.97414586584250062f,
		0.97781169577969584f,
		0.98105641710392333f,
		0.98391328975491177f,
		0.98641503193166202f,
		0.98859353733226141f,
		0.99047962335771556f,
		0.9921028127769449f,
		0.99349115056397752f,
		0.99467105680259038f,
		0.9956672157341897f,
		0.99650250022834352f,
		0.99719793020823266f,
		0.99777266288955657f,
		0.99824401211201486f,
		0.99862749357391212f,
		0.99893689243401962f,
		0.99918434952623147f,
		0.99938046234161726f,
		0.99953439696357238f,
		0.99965400728430465f,
		0.99974595807027455f,
		0.99981584876278362f,
		0.99986833527824281f,
		0.99990724749057802f,
		0.99993570051598468f,
		0.99995619835942084f,
		0.99997072890647543f,
		0.9999808496399144f,
		0.99998776381655818f,
		0.99999238714961569f,
		0.99999540529959718f,
		0.99999732268176988f,
		0.99999850325054862f,
		0.99999920402413744f,
		0.9999996021706401f,
		0.99999981649545566f,
		0.99999992415545547f,
		0.99999997338493041f,
		0.99999999295825959f,
		0.99999999904096815f
	};

}
