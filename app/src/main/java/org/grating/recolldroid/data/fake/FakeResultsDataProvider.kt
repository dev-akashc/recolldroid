/* Copyright (C) 2024 Graham Bygrave
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the
 *   Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.grating.recolldroid.data.fake

import kotlinx.serialization.json.Json
import org.grating.recolldroid.data.RecollSearchResult

object FakeResultsDataProvider {

    fun getSampleResults(): List<RecollSearchResult> {
        // Needs composable context.
//        val context = LocalContext.current
//        val json = context.resources.openRawResource(R.raw.sample_query_results)
//            .bufferedReader().use { it.readText() }
        return Json.decodeFromString<List<RecollSearchResult>>(getResultsJson());
    }

    fun getFirstResult(): RecollSearchResult {
        return getSampleResults().first()
    }

    private fun getResultsJson(): String {
        return """
[
  {
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/An Introduction to Support Vector Machines.djvu|",
    "title": "An Introduction to Support Vector Machines",
    "fbytes": "3260101",
    "sig": "32601011282867159",
    "url": "file:///data/media/books/Machine Learning/An Introduction to Support Vector Machines.djvu",
    "abstract": " An Introduction to Support Vector Machines  This book is the first comprehensive introduction to Support Vector Machines  (SVMs), a new generation learning system based on recent advances in statistical  learning theory. SVMs deliver state-of-the",
    "dbytes": "420948",
    "filename": "An Introduction to Support Vector Machines.djvu",
    "relevancyrating": " 25%",
    "fmtime": "01282867159",
    "mtype": "image/vnd.djvu",
    "origcharset": "UTF-8",
    "mtime": "01282867159",
    "pcbytes": "3260101",
    "snippets_abstract": " [P. 80] Chapter 6 that the †support‡ †vectors‡ of a †Support‡ †Vector‡  †Machine‡... [P. 199] 78  supervised learning, 1  †Support‡ †Vector‡ †Machines‡, 7, 93  †support‡ †vectors‡, 97  target function, 2... [P. 17] of these problems.  \u001F1.5 †Support‡ †Vector‡ †Machines‡ for Learning  †Support‡ †Vector‡... [P. 124] regression function  114  6 †Support‡ †Vector‡ †Machines‡  †Vector‡ †Machines‡ it will ensure sparseness... [P. 5] 112  6.1.3 Linear Programming †Support‡ †Vector‡ †Machines‡  112  †Support‡... [P. 18] Mitchell’s book †Machine‡ learning [99]. †Support‡  †Vector‡ †Machines‡ were introduced by Vapnik... [P. 122] 112  6 †Support‡ †Vector‡ †Machines‡  \u001F6.1.3 Linear Programming †Support‡ †Vector‡ †Machines‡  Rather than using generalisation... [P. 114] darker shading  \u001F 104  6 †Support‡ †Vector‡ †Machines‡  to allow thc margin...",
    "snippets": [
        {
          "p": 80,
          "kw": "support",
          "s": "Chapter 6 that the †support‡ †vectors‡ of a †Support‡ †Vector‡  †Machine‡"
        },
        {
          "p": 199,
          "kw": "Support",
          "s": "78  supervised learning, 1  †Support‡ †Vector‡ †Machines‡, 7, 93  †support‡ †vectors‡, 97  target function, 2"
        },
        {
          "p": 17,
          "kw": "Support",
          "s": "of these problems.  \u001F1.5 †Support‡ †Vector‡ †Machines‡ for Learning  †Support‡ †Vector‡"
        },
        {
          "p": 124,
          "kw": "Support",
          "s": "regression function  114  6 †Support‡ †Vector‡ †Machines‡  †Vector‡ †Machines‡ it will ensure sparseness"
        },
        {
          "p": 5,
          "kw": "Support",
          "s": "112  6.1.3 Linear Programming †Support‡ †Vector‡ †Machines‡  112  †Support‡"
        },
        {
          "p": 18,
          "kw": "Support",
          "s": "Mitchell’s book †Machine‡ learning [99]. †Support‡  †Vector‡ †Machines‡ were introduced by Vapnik"
        },
        {
          "p": 122,
          "kw": "Support",
          "s": "112  6 †Support‡ †Vector‡ †Machines‡  \u001F6.1.3 Linear Programming †Support‡ †Vector‡ †Machines‡  Rather than using generalisation"
        },
        {
          "p": 114,
          "kw": "Support",
          "s": "darker shading  \u001F 104  6 †Support‡ †Vector‡ †Machines‡  to allow thc margin"
        }
    ]
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2008/JaewonYang-ProteinSecondaryStructurePrediction.pdf",
    "sig": "4240401272307844",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2008/JaewonYang-ProteinSecondaryStructurePrediction.pdf|",
    "title": "Microsoft Word - JaewonYang_ProteinSecondaryStructurePrediction",
    "fbytes": "424040",
    "abstract": "  CS229 Final Project, Dec 2008 1 Protein Secondary Structure Prediction based on Neural Network Models and Support Vector Machines Jaewon Yang Departments of Electrical Engineering, Stanford University jaewony@stanford.edu these problems [1].",
    "author": "Tae-Ho,PScript5.dll Version 5.2",
    "caption": "Microsoft Word - JaewonYang_ProteinSecondaryStructurePrediction",
    "dbytes": "28124",
    "filename": "JaewonYang-ProteinSecondaryStructurePrediction.pdf",
    "relevancyrating": " 24%",
    "fmtime": "01272307844",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307844",
    "pcbytes": "424040"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Advances in Large Margin Classifiers, Smola, Bartlett.pdf",
    "sig": "44245691217389286",
    "rclmbreaks": "955,1,1307,1,12156,1,14105,1,14109,1,27356,1,41371,1,48863,1,62555,1,77099,1,77101,1,96350,1,107955,1,111687,1,118299,1,124089,1,131142,1,138447,1,145991,1",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Advances in Large Margin Classifiers, Smola, Bartlett.pdf|",
    "title": "Advances in Large-Margin Classifiers",
    "fbytes": "4424569",
    "abstract": "ISBN-13: 9780262194488",
    "author": "Peter J. Bartlett, Bernhard Schölkopf, Dale Schuurmans, Alex J Smola,dvips(k) 5.86 Copyright 1999 Radical Eye Software",
    "caption": "Advances in Large-Margin Classifiers",
    "dbytes": "834957",
    "filename": "Advances in Large Margin Classifiers, Smola, Bartlett.pdf",
    "relevancyrating": " 24%",
    "fmtime": "01217389286",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01217389286",
    "pcbytes": "4424569"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2007/Antonellis_Papadimitriou_project.pdf",
    "sig": "3531961272307844",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2007/Antonellis_Papadimitriou_project.pdf|",
    "title": "antonellis_writeup.dvi",
    "fbytes": "353196",
    "abstract": "  Patent Cases Docket Classification Ioannis Antonellis ∗ Panagiotis Papadimitriou † Abstract We contribute to the Intellectual Property Litigation Clearinghouse (IPLC) project by providing an extensive experimental evaluation of two",
    "author": "dvips(k) 5.95b Copyright 2005 Radical Eye Software",
    "caption": "antonellis_writeup.dvi",
    "dbytes": "21851",
    "filename": "Antonellis_Papadimitriou_project.pdf",
    "relevancyrating": " 24%",
    "fmtime": "01272307844",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307844",
    "pcbytes": "353196"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2008/LehnertFriedrich-MachineLearningClassificationOfMaliciousNetworkTraffic.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2008/LehnertFriedrich-MachineLearningClassificationOfMaliciousNetworkTraffic.pdf|",
    "title": "",
    "fbytes": "98813",
    "sig": "988131272307844",
    "abstract": "  MACHINE LEARNING CLASSIFICATION OF MALICIOUS NETWORK TRAFFIC KEITH LEHNERT AND ERIC FRIEDRICH 1. Introduction 1.1. Intrusion Detection Systems. In our society, information systems are everywhere. They are used by corporations to store",
    "author": "TeX",
    "dbytes": "10929",
    "filename": "LehnertFriedrich-MachineLearningClassificationOfMaliciousNetworkTraffic.pdf",
    "relevancyrating": " 23%",
    "fmtime": "01272307844",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307844",
    "pcbytes": "98813"
  },
  {
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Linear Programming Boosting via Column Generation, Demiriz, Bennet, Shawe-Taylor.pdf|",
    "title": "",
    "fbytes": "400446",
    "sig": "4004461281751982",
    "url": "file:///data/media/books/Machine Learning/Linear Programming Boosting via Column Generation, Demiriz, Bennet, Shawe-Taylor.pdf",
    "abstract": "  Machine Learning, 46, 225–254, 2002 c 2002 Kluwer Academic Publishers. Manufactured in The Netherlands. \u0002 Linear Programming Boosting via Column Generation AYHAN DEMIRIZ demira@rpi.edu Department of Decision Sciences and Eng. Systems,",
    "dbytes": "77703",
    "filename": "Linear Programming Boosting via Column Generation, Demiriz, Bennet, Shawe-Taylor.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01281751982",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01281751982",
    "pcbytes": "400446"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2009/JonesAoun.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/JonesAoun.pdf|",
    "title": "",
    "fbytes": "890190",
    "sig": "8901901272307843",
    "abstract": "  Learning 3D Point Cloud Histograms CS229 Machine Learning Project Brian JONES Michel AOUN December 11, 2009 Abstract In this paper we show how using histograms based on the angular relationships between a subset of point normals in a 3D point",
    "author": "TeX",
    "dbytes": "10355",
    "filename": "JonesAoun.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "890190"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2008/MeadorUhlig-ContentBasedFeaturesInComposerIdentifiation.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2008/MeadorUhlig-ContentBasedFeaturesInComposerIdentifiation.pdf|",
    "title": "",
    "fbytes": "268685",
    "sig": "2686851272307844",
    "abstract": "  Content-Based Features in the Composer Identification Problem CS 229 Final Project Sean Meador (smeador@stanford.edu) Karl Uhlig (knuhlig@stanford.edu) December 12, 2008 1 Overview Classification of digital music (also called Music",
    "author": "TeX",
    "dbytes": "12515",
    "filename": "MeadorUhlig-ContentBasedFeaturesInComposerIdentifiation.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307844",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307844",
    "pcbytes": "268685"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2009/ArakiLeeKuefler.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/ArakiLeeKuefler.pdf|",
    "title": "",
    "fbytes": "138565",
    "sig": "1385651272307843",
    "abstract": "  Classifying Relationships Between Nouns Jun Araki Heeyoung Lee Erik Kuefler Abstract In this paper we develop a system for classifying relationships between noun pairs. This is accomplished by finding the dependency paths between the nouns in",
    "author": "TeX",
    "dbytes": "22818",
    "filename": "ArakiLeeKuefler.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "138565"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2009/Choo.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/Choo.pdf|",
    "title": "",
    "fbytes": "311302",
    "sig": "3113021272307843",
    "abstract": "  Practical Option Pricing with Support Vector Regression and MART by Ian I-En Choo Stanford University 1. Introduction The Black-Scholes [BS73] approach to option pricing is arguably one of the most important ideas in all of finance today. From",
    "author": "Ian Choo,Microsoft® Office Word 2007",
    "dbytes": "18918",
    "filename": "Choo.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "311302"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Natural Language Processing/Feature Selection using Linear Classifier Weights: Interaction with Classification Models, Mladenić ",
    "sig": "3054111279907638",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Natural Language Processing/Feature Selection using Linear Classifier Weights: Interaction wiR1tTIpE5n7LCMWPtqYg09Q",
    "title": "Microsoft Word - p181-mladenic-final2.doc",
    "fbytes": "305411",
    "abstract": "  Feature Selection using Linear Classifier Weights: Interaction with Classification Models Dunja Mladenić Janez Brank Marko Grobelnik Natasa Milic-Frayling Jožef Stefan Institute Ljubljana, Slovenia Tel.: +386 1 477 3900 Jožef Stefan",
    "author": "Janez,PScript5.dll Version 5.2",
    "caption": "Microsoft Word - p181-mladenic-final2.doc",
    "dbytes": "40593",
    "filename": "Feature Selection using Linear Classifier Weights: Interaction with Classification Models, Mladenić   et al.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01279907638",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01279907638",
    "pcbytes": "305411"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2009/Styer.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/Styer.pdf|",
    "title": "",
    "fbytes": "236196",
    "sig": "2361961272307843",
    "abstract": "  Multiclass SVMs for Olfactory Classi\u001Ccation CS229 Final Project Report Michael Styer Computer Science Department, Stanford University 1 Introduction The study of olfactory classi\u001Ccation has many attractions, philosophical and theoretical as",
    "author": "TeX",
    "dbytes": "26482",
    "filename": "Styer.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "236196"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2006/GottliebSalisburyShekVaidyanathan-DetectingCorporateFraud.pdf",
    "sig": "1842231272307844",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2006/GottliebSalisburyShekVaidyanathan-DetectingCorporateFraud.pdf|",
    "title": "GottliebSalisburyShekVaidyanathan-DetectingCorporateFraud.dvi",
    "fbytes": "184223",
    "abstract": "  Detecting Corporate Fraud: An Application of Machine Learning Ophir Gottlieb, Curt Salisbury, Howard Shek, Vishal Vaidyanathan December 15, 2006 ABSTRACT niques for this purpose. Algorithms for automatated detection of patterns of fraud are",
    "author": "dvips(k) 5.95a Copyright 2005 Radical Eye Software",
    "caption": "GottliebSalisburyShekVaidyanathan-DetectingCorporateFraud.dvi",
    "dbytes": "19644",
    "filename": "GottliebSalisburyShekVaidyanathan-DetectingCorporateFraud.pdf",
    "relevancyrating": " 22%",
    "fmtime": "01272307844",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307844",
    "pcbytes": "184223"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2009/NewburgerLiuTang.pdf",
    "sig": "5220801272307843",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/NewburgerLiuTang.pdf|",
    "title": "Microsoft Word - Draft_CS229_DN_Edit.doc",
    "fbytes": "522080",
    "abstract": "  SNPrints: Defining SNP signatures for prediction of onset in complex diseases Linda Liu, Biomedical Informatics, Stanford University Daniel Newburger, Biomedical Informatics, Stanford University Grace Tang, Bioengineering, Stanford University",
    "author": "lyl,Microsoft Word",
    "caption": "Microsoft Word - Draft_CS229_DN_Edit.doc",
    "dbytes": "15672",
    "filename": "NewburgerLiuTang.pdf",
    "relevancyrating": " 21%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "522080"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Kernel Methods for Pattern Analysis.pdf",
    "sig": "31659811257881708",
    "rclmbreaks": "328,1,1493,1,2717,1,39716,1,39721,1,93923,1,93927,1",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Kernel Methods for Pattern Analysis.pdf|",
    "title": "Kernel Methods for Pattern Analysis",
    "fbytes": "3165981",
    "abstract": "  This page intentionally left blank Kernel Methods for Pattern Analysis Pattern Analysis is the process of ﬁnding general relations in a set of data, and forms the core of many disciplines, from neural networks to so-called syntactical",
    "author": "John Shawe-Taylor and Nello Cristianini,PScript5.dll Version 5.2",
    "caption": "Kernel Methods for Pattern Analysis",
    "dbytes": "869707",
    "filename": "Kernel Methods for Pattern Analysis.pdf",
    "relevancyrating": " 21%",
    "fmtime": "01257881708",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01257881708",
    "pcbytes": "3165981"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf|",
    "title": "",
    "fbytes": "228575",
    "sig": "2285751281364767",
    "abstract": "  Tackling the Poor Assumptions of Naive Bayes Text Classifiers Jason D. M. Rennie jrennie@mit.edu Lawrence Shih kai@mit.edu Jaime Teevan teevan@mit.edu David R. Karger karger@mit.edu Artificial Intelligence Laboratory; Massachusetts Institute of",
    "author": "TeX",
    "dbytes": "34970",
    "filename": "Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf",
    "relevancyrating": " 21%",
    "fmtime": "01281364767",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01281364767",
    "pcbytes": "228575"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Natural Language Processing/Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Natural Language Processing/Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf|",
    "title": "",
    "fbytes": "228575",
    "sig": "2285751281364767",
    "abstract": "  Tackling the Poor Assumptions of Naive Bayes Text Classifiers Jason D. M. Rennie jrennie@mit.edu Lawrence Shih kai@mit.edu Jaime Teevan teevan@mit.edu David R. Karger karger@mit.edu Artificial Intelligence Laboratory; Massachusetts Institute of",
    "author": "TeX",
    "dbytes": "34970",
    "filename": "Tackling the Poor Assumptions of Naive Bayes Text Classifiers.pdf",
    "relevancyrating": " 21%",
    "fmtime": "01281364767",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01281364767",
    "pcbytes": "228575"
  },
  {
    "url": "file:///data/media/books/Machine Learning/Machine Learning and Its Applications.djvu",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/Machine Learning and Its Applications.djvu|",
    "rclmbreaks": "131448,2",
    "title": "",
    "fbytes": "9542476",
    "sig": "95424761274728948",
    "abstract": " Georgios Paliouras Vangelis Karkaletsis _ Constantine D. Spyropoulos (Eds.) xs .§ .2 .» ON I O § Machme Learnmg 2 • • E and Its A   Incatnons Advanced Lectures © “?/- E 2* v® SQ,   vvA [YQ AG 53 S  e\u003E ca ' `     Springer Lecture",
    "dbytes": "821474",
    "filename": "Machine Learning and Its Applications.djvu",
    "relevancyrating": " 21%",
    "fmtime": "01274728948",
    "mtype": "image/vnd.djvu",
    "origcharset": "UTF-8",
    "mtime": "01274728948",
    "pcbytes": "9542476"
  },
  {
    "url": "file:///data/media/books/Finance/Applications of Data Mining in E-Business and Finance, Soares, Peng et. al..pdf",
    "ipath": "",
    "rcludi": "/data/media/books/Finance/Applications of Data Mining in E-Business and Finance, Soares, Peng et. al..pdf|",
    "title": "",
    "fbytes": "4783798",
    "sig": "47837981274009894",
    "abstract": "  APPLICATIONS OF DATA MINING IN E-BUSINESS AND FINANCE Frontiers in Artificial Intelligence and Applications FAIA covers all aspects of theoretical and applied artificial intelligence research in the form of monographs, doctoral dissertations,",
    "author": "IOS Press",
    "dbytes": "362908",
    "filename": "Applications of Data Mining in E-Business and Finance, Soares, Peng et. al..pdf",
    "relevancyrating": " 21%",
    "fmtime": "01274009894",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01274009894",
    "pcbytes": "4783798"
  },
  {
    "url": "file:///data/media/books/Machine Learning/.cs229/proj2005/HandyRajaraman-SemanticQueryAnalysis.pdf",
    "sig": "592511272307843",
    "ipath": "",
    "rcludi": "/data/media/books/Machine Learning/.cs229/proj2005/HandyRajaraman-SemanticQueryAnalysis.pdf|",
    "title": "Microsoft Word - finalReport.doc",
    "fbytes": "59251",
    "abstract": "  Semantic Extensions to Syntactic Analysis of Queries Ben Handy, Rohini Rajaraman Abstract We intend to show that leveraging semantic features can improve precision and recall of query results in information retrieval (IR) systems. Nearly all",
    "author": "Administrator,PScript5.dll Version 5.2",
    "caption": "Microsoft Word - finalReport.doc",
    "dbytes": "19261",
    "filename": "HandyRajaraman-SemanticQueryAnalysis.pdf",
    "relevancyrating": " 21%",
    "fmtime": "01272307843",
    "mtype": "application/pdf",
    "origcharset": "CP1252",
    "mtime": "01272307843",
    "pcbytes": "59251"
  }
]
        """.trimIndent()
    }
}

